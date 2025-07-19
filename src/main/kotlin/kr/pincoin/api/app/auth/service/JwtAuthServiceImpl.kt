package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.request.UserCreateRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.domain.coordinator.user.UserResourceCoordinator
import kr.pincoin.api.domain.user.error.AuthErrorCode
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.model.User
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

/**
 * JWT 전용 인증 서비스 구현체 (레거시)
 *
 * 순수 JWT 기반 인증만 지원하며 Keycloak 연동 없음
 * 기존 시스템에서 사용하던 전통적인 JWT 인증 방식
 */
@Service
class JwtAuthServiceImpl(
    private val userResourceCoordinator: UserResourceCoordinator,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    private val eventPublisher: ApplicationEventPublisher,
) : AuthService {

    private val logger = KotlinLogging.logger {}

    /**
     * JWT 기반 로그인 처리
     * Keycloak 연동 없이 순수 JWT 토큰만 발급
     */
    @Transactional
    override fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user = try {
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (_: Exception) {
            publishLoginFailureEvent(
                email = request.email,
                servletRequest = servletRequest,
                reason = "사용자 찾을 수 없음"
            )
            logger.error { "사용자 없음: ${request.email}" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            publishLoginFailureEvent(
                email = request.email,
                servletRequest = servletRequest,
                reason = "비밀번호 불일치"
            )
            logger.error { "비밀번호 불일치: ${request.email}" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        // 로그인 성공 이벤트 발행
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
                email = request.email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "JWT 로그인: 성공",
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
     * JWT 리프레시 토큰으로 새로운 토큰 쌍 발급
     */
    @Transactional
    override fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        try {
            validateRefreshToken(refreshToken, servletRequest)
        } catch (e: JwtAuthenticationException) {
            publishRefreshFailureEvent(servletRequest, "토큰 검증 실패")
            throw e
        }

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: run {
                    publishRefreshFailureEvent(servletRequest, "이메일 검증 실패")
                    throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
                }

            // DB에서 최신 사용자 정보 조회
            val user = try {
                userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                    ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            } catch (_: Exception) {
                publishRefreshFailureEvent(servletRequest, "사용자 없음", email)
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = user.id,
                    email = email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = true,
                    reason = "JWT 리프레시: 성공",
                )
            )

            // 새 토큰 쌍 생성
            val newAccessToken = jwtTokenProvider.createAccessToken(user)
            val newRefreshToken = jwtTokenProvider.createRefreshToken()

            try {
                delete(refreshToken)
                saveRefreshTokenInfo(newRefreshToken, email, servletRequest)
            } catch (e: Exception) {
                publishRefreshFailureEvent(servletRequest, "갱신 오류", email, user.id)
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
     * JWT 로그아웃 처리
     * Redis에서 리프레시 토큰 정보 삭제
     */
    override fun logout(refreshToken: String) {
        with(redisTemplate) {
            val email = opsForHash<String, String>().get(refreshToken, RedisKey.EMAIL) ?: return

            delete(refreshToken)
            delete(email)

            logger.debug { "JWT 로그아웃 완료: $email" }
        }
    }

    /**
     * 순수 JWT 전용 사용자 생성
     * Keycloak 동기화 없이 로컬 DB에만 저장
     */
    @Transactional
    override fun createUser(request: UserCreateRequest): User {
        logger.info { "JWT 전용 사용자 생성: ${request.email}" }
        return userResourceCoordinator.createUser(request)
    }

    /**
     * Redis에 리프레시 토큰 관련 정보 저장
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

    /**
     * 로그인 실패 이벤트 발행
     */
    private fun publishLoginFailureEvent(
        email: String,
        servletRequest: HttpServletRequest,
        reason: String
    ) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = null,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = false,
                reason = "JWT 로그인: 실패 - $reason",
            )
        )
    }

    /**
     * 리프레시 실패 이벤트 발행
     */
    private fun publishRefreshFailureEvent(
        servletRequest: HttpServletRequest,
        reason: String,
        email: String? = null,
        userId: Int? = null
    ) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = userId,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = false,
                reason = "JWT 리프레시: 실패 - $reason",
            )
        )
    }

    companion object {
        private const val UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    }
}