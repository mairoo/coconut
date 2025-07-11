package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.domain.user.error.AuthErrorCode
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.domain.user.vo.TokenPair
import kr.pincoin.api.global.constant.RedisKey
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.exception.JwtAuthenticationException
import kr.pincoin.api.global.properties.JwtProperties
import kr.pincoin.api.global.security.jwt.JwtTokenProvider
import kr.pincoin.api.global.utils.IpUtils
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.context.ApplicationEventPublisher
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    private val eventPublisher: ApplicationEventPublisher,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 사용자 로그인 처리 및 토큰 발급
     */
    @Transactional
    fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user = try {
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (e: Exception) {
            // 로그인 실패: 비밀번호 로그인 사용자 찾을 수 없음
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                )
            )
            logger.error { "사용자 없음" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            // 로그인 실패: 비밀번호 불일치
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                )
            )
            logger.error { "비밀번호 틀림" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        // 로그인 성공
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
            )
        )

        val accessToken = jwtTokenProvider.createAccessToken(user)

        // 자동 로그인 요청시에만 리프레시 토큰 발급
        return if (request.rememberMe) {
            // 기존 리프레시 토큰 삭제
            with(redisTemplate) {
                opsForValue().get(user.email)?.let { oldRefreshToken ->
                    delete(oldRefreshToken)
                }
            }

            val refreshToken = jwtTokenProvider.createRefreshToken()
            saveRefreshTokenInfo(refreshToken, user.email, servletRequest)

            TokenPair(
                AccessTokenResponse.of(accessToken, jwtProperties.accessTokenExpiresIn),
                refreshToken
            )
        } else {
            TokenPair(
                AccessTokenResponse.of(accessToken, jwtProperties.accessTokenExpiresIn),
                null
            )
        }
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 토큰 쌍 발급
     */
    @Transactional
    fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        try {
            validateRefreshToken(refreshToken, servletRequest)
        } catch (e: JwtAuthenticationException) {
            // 리프레시 실패: 토큰 검증 실패
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                )
            )
            throw e
        }

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: run {
                    // 리프레시 실패: 토큰에서 이메일 조회 불가
                    eventPublisher.publishEvent(
                        LoginEvent(
                            ipAddress = IpUtils.getClientIp(servletRequest),
                            userId = null,
                        )
                    )
                    throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
                }

            // DB에서 최신 사용자 정보 조회
            val user = try {
                userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                    ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            } catch (e: Exception) {
                // 리프레시 실패: 리프레시 로그인 사용자 없음
                eventPublisher.publishEvent(
                    LoginEvent(
                        ipAddress = IpUtils.getClientIp(servletRequest),
                        userId = null,
                    )
                )
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

            // 리프레시 성공
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = user.id,
                )
            )

            // 최신 사용자 정보로 새 액세스 토큰 생성
            val newAccessToken = jwtTokenProvider.createAccessToken(user)
            val newRefreshToken = jwtTokenProvider.createRefreshToken()

            try {
                delete(refreshToken)
                saveRefreshTokenInfo(newRefreshToken, email, servletRequest)
            } catch (e: Exception) {
                // 리프레시 실패: 토큰 갱신 과정에서 오류 (Redis 연결 문제 등)
                eventPublisher.publishEvent(
                    LoginEvent(
                        ipAddress = IpUtils.getClientIp(servletRequest),
                        userId = null,
                    )
                )
                throw e
            }

            return TokenPair(
                accessToken = AccessTokenResponse.of(
                    newAccessToken,
                    jwtProperties.accessTokenExpiresIn
                ),
                refreshToken = newRefreshToken
            )
        }
    }

    /**
     * 로그아웃 처리 Redis에서 리프레시 토큰과 관련 정보 삭제
     */
    fun logout(refreshToken: String) {
        with(redisTemplate) {
            val email = opsForHash<String, String>().get(refreshToken, RedisKey.EMAIL) ?: return

            delete(refreshToken)
            delete(email)
        }
    }

    /**
     * Redis에 리프레시 토큰 관련 정보 저장
     * - 리프레시 토큰을 키로 하는 해시: 이메일, IP 주소 저장
     * - 이메일을 키로 하는 문자열: 리프레시 토큰 저장
     */
    private fun saveRefreshTokenInfo(
        refreshToken: String,
        email: String,
        request: HttpServletRequest
    ) {
        val clientIp = IpUtils.getClientIp(request)

        with(redisTemplate) {
            // 리프레시 토큰 해시에 이메일과 IP 주소 저장
            opsForHash<String, String>()
                .putAll(
                    refreshToken,
                    mapOf(RedisKey.EMAIL to email, RedisKey.IP_ADDRESS to clientIp)
                )

            // 리프레시 토큰 만료 시간 설정
            expire(refreshToken, jwtProperties.refreshTokenExpiresIn, TimeUnit.SECONDS)

            // 이메일로 리프레시 토큰 조회를 위한 키-값 저장
            opsForValue().set(
                email,
                refreshToken,
                jwtProperties.refreshTokenExpiresIn,
                TimeUnit.SECONDS
            )
        }
    }

    /**
     * 리프레시 토큰 유효성 검증
     * - UUID 형식 검증
     * - Redis에 저장된 토큰인지 확인
     * - 요청 IP와 저장된 IP 일치 여부 확인
     */
    private fun validateRefreshToken(refreshToken: String, request: HttpServletRequest) {
        if (!refreshToken.matches(UUID_PATTERN.toRegex())) {
            throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }

        with(redisTemplate.opsForHash<String, String>()) {
            val email = get(refreshToken, RedisKey.EMAIL)
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)

            val storedIp = get(refreshToken, RedisKey.IP_ADDRESS)
            val currentIp = IpUtils.getClientIp(request)
            if (storedIp == null || storedIp != currentIp) {
                throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
            }
        }
    }

    companion object {
        private const val UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    }
}