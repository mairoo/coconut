package kr.co.pincoin.api.app.auth.service

import jakarta.servlet.http.HttpServletRequest
import kr.co.pincoin.api.app.auth.request.SignInRequest
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse
import kr.co.pincoin.api.domain.user.event.LoginEvent
import kr.co.pincoin.api.domain.user.repository.UserRepository
import kr.co.pincoin.api.domain.user.vo.TokenPair
import kr.co.pincoin.api.global.constant.RedisKey
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.JwtAuthenticationException
import kr.co.pincoin.api.global.exception.code.AuthErrorCode
import kr.co.pincoin.api.global.properties.JwtProperties
import kr.co.pincoin.api.global.security.jwt.JwtTokenProvider
import kr.co.pincoin.api.global.utils.IpUtils
import kr.co.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
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
    /**
     * 사용자 로그인 처리 및 토큰 발급
     *
     * @param request 로그인 요청 정보 (이메일, 비밀번호, 자동 로그인 여부)
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (액세스 토큰, 리프레시 토큰)
     * @throws BusinessException 이메일이나 비밀번호가 일치하지 않는 경우
     */
    @Transactional
    fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user = try {
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (e: Exception) {
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                    email = request.email,
                    username = null,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "비밀번호 로그인: 사용자를 찾을 수 없음",
                    userId = null,
                )
            )
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                    email = request.email,
                    username = null,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "비밀번호 로그인: 비밀번호 불일치",
                    userId = user.id,
                )
            )
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                email = user.email,
                username = user.username,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "비밀번호 로그인",
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
     *
     * @param refreshToken 기존 리프레시 토큰
     * @param servletRequest IP 주소 확인용 HTTP 요청 객체
     * @return TokenPair (새로운 액세스 토큰, 새로운 리프레시 토큰)
     * @throws JwtAuthenticationException 토큰이 유효하지 않거나 IP가 불일치하는 경우
     */
    @Transactional
    fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        try {
            validateRefreshToken(refreshToken, servletRequest)
        } catch (e: JwtAuthenticationException) {
            // 리프레시 토큰 검증 실패 이벤트 발행
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                    email = null,
                    username = null,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "리프레시: 토큰 검증 실패",
                    userId = null
                )
            )
            throw e
        }

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: run {
                    // 토큰에서 이메일을 조회할 수 없는 경우
                    eventPublisher.publishEvent(
                        LoginEvent(
                            ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                            email = null,
                            username = null,
                            userAgent = servletRequest.getHeader("User-Agent"),
                            isSuccessful = false,
                            reason = "리프레시: 이메일을 찾을 수 없음",
                            userId = null
                        )
                    )
                    throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
                }

            // DB에서 최신 사용자 정보 조회
            val user = try {
                userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                    ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            } catch (e: Exception) {
                eventPublisher.publishEvent(
                    LoginEvent(
                        ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                        email = email,
                        username = null,
                        userAgent = servletRequest.getHeader("User-Agent"),
                        isSuccessful = false,
                        reason = "리프레시: 사용자를 찾을 수 없음",
                        userId = null,
                    )
                )
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

            // 리프레시 로그인 성공
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                    email = email,
                    username = user.username,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = true,
                    reason = "리프레시",
                    userId = user.id
                )
            )

            // 최신 사용자 정보로 새 액세스 토큰 생성
            val newAccessToken = jwtTokenProvider.createAccessToken(user)
            val newRefreshToken = jwtTokenProvider.createRefreshToken()

            try {
                delete(refreshToken)
                saveRefreshTokenInfo(newRefreshToken, email, servletRequest)
            } catch (e: Exception) {
                // 토큰 갱신 과정에서 오류 발생 (Redis 연결 문제 등)
                eventPublisher.publishEvent(
                    LoginEvent(
                        ipAddress = IpUtils.parseInetAddress(IpUtils.getClientIp(servletRequest)),
                        email = email,
                        username = user.username,
                        userAgent = servletRequest.getHeader("User-Agent"),
                        isSuccessful = false,
                        reason = "리프레시: 토큰 갱신 중 오류",
                        userId = user.id
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