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
import kr.pincoin.api.global.properties.KeycloakProperties
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
    private val userResourceCoordinator: UserResourceCoordinator,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val keycloakProperties: KeycloakProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    private val eventPublisher: ApplicationEventPublisher,
    private val keycloakAuthService: KeycloakAuthService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 사용자 로그인 처리 및 토큰 발급
     * Keycloak이 활성화된 경우 먼저 Keycloak 인증을 시도하고, 실패하면 기존 JWT 인증으로 폴백
     */
    @Transactional
    fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        // 1. Keycloak 인증 시도 (활성화된 경우)
        keycloakAuthService.loginWithKeycloak(request, servletRequest)?.let { tokenPair ->
            return tokenPair
        }

        // 2. 기존 JWT 인증 (Keycloak 비활성화 또는 인증 실패시)
        return loginWithJwt(request, servletRequest)
    }

    /**
     * 기존 JWT 기반 로그인 로직
     */
    private fun loginWithJwt(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user = try {
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (_: Exception) {
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                    email = request.email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "비밀번호 로그인: 사용자 찾을 수 없음",
                )
            )
            logger.error { "사용자 없음" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                    email = request.email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "비밀번호 로그인: 비밀번호 불일치",
                )
            )
            logger.error { "비밀번호 불일치" }
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
                reason = "비밀번호 로그인: 성공",
            )
        )

        // 🆕 로그인 성공 후 Keycloak 동기화 시도 (백그라운드)
        try {
            val syncResult = keycloakAuthService.syncUserToKeycloak(user, request.password)
            if (syncResult) {
                logger.info { "Keycloak 동기화 성공: ${user.email}" }
            } else {
                logger.debug { "Keycloak 동기화 스킵 (설정 비활성화): ${user.email}" }
            }
        } catch (e: Exception) {
            // 동기화 실패해도 로그인은 성공으로 처리
            logger.warn(e) { "Keycloak 동기화 실패하지만 로그인은 성공: ${user.email}" }
        }

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
     * Keycloak 활성화 시 Keycloak 리프레시 토큰 우선 처리
     */
    @Transactional
    fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        // 🆕 Keycloak이 활성화된 경우 Keycloak 리프레시 토큰으로 처리 시도
        if (keycloakProperties.enabled) {
            keycloakAuthService.refreshWithKeycloak(refreshToken, servletRequest)?.let { tokenPair ->
                return tokenPair
            }
        }

        // 기존 JWT 리프레시 토큰 처리 (폴백)
        return refreshWithJwt(refreshToken, servletRequest)
    }

    /**
     * 기존 JWT 리프레시 토큰 처리 로직
     */
    private fun refreshWithJwt(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        try {
            validateRefreshToken(refreshToken, servletRequest)
        } catch (e: JwtAuthenticationException) {
            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                    email = null,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "리프레시: 토큰 검증 실패: $refreshToken",
                )
            )
            throw e
        }

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: run {
                    eventPublisher.publishEvent(
                        LoginEvent(
                            ipAddress = IpUtils.getClientIp(servletRequest),
                            userId = null,
                            email = null,
                            userAgent = servletRequest.getHeader("User-Agent"),
                            isSuccessful = false,
                            reason = "리프레시: 이메일 검증 실패",
                        )
                    )
                    throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
                }

            // DB에서 최신 사용자 정보 조회
            val user = try {
                userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                    ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            } catch (_: Exception) {
                eventPublisher.publishEvent(
                    LoginEvent(
                        ipAddress = IpUtils.getClientIp(servletRequest),
                        userId = null,
                        email = email,
                        userAgent = servletRequest.getHeader("User-Agent"),
                        isSuccessful = false,
                        reason = "리프레시: 사용자 없음",
                    )
                )
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = user.id,
                    email = email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = true,
                    reason = "리프레시: 성공",
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
                        userId = user.id,
                        email = email,
                        userAgent = servletRequest.getHeader("User-Agent"),
                        isSuccessful = false,
                        reason = "리프레시: 갱신 오류",
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
     * 로그아웃 처리
     * Keycloak 활성화 시 Keycloak 로그아웃도 함께 처리
     */
    fun logout(refreshToken: String) {
        // 🆕 Keycloak이 활성화된 경우 Keycloak 로그아웃 시도
        if (keycloakProperties.enabled) {
            try {
                keycloakAuthService.logoutFromKeycloak(refreshToken)
            } catch (e: Exception) {
                logger.warn(e) { "Keycloak 로그아웃 실패하지만 계속 진행" }
            }
        }

        // 기존 JWT 리프레시 토큰 삭제 (Redis)
        logoutJwt(refreshToken)
    }

    /**
     * 기존 JWT 로그아웃 처리
     */
    private fun logoutJwt(refreshToken: String) {
        with(redisTemplate) {
            val email = opsForHash<String, String>().get(refreshToken, RedisKey.EMAIL) ?: return

            delete(refreshToken)
            delete(email)
        }
    }

    fun createUser(
        request: UserCreateRequest,
    ): User =
        userResourceCoordinator.createUser(request)

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