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
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * Keycloak 전용 인증 서비스 구현체
 *
 * 순수 Keycloak 기반 인증만 지원하며 JWT 폴백 없음
 * 완전히 Keycloak으로 이관된 시스템에서 사용
 */
@Service
class KeycloakAuthServiceImpl(
    private val keycloakProperties: KeycloakProperties,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val userRepository: UserRepository,
    private val userResourceCoordinator: UserResourceCoordinator,
    private val eventPublisher: ApplicationEventPublisher,
    private val keycloakWebClient: WebClient,
    private val redisTemplate: RedisTemplate<String, String>,
) : AuthService {

    private val logger = KotlinLogging.logger {}

    /**
     * Keycloak 전용 로그인 처리
     * JWT 폴백 없이 Keycloak 인증만 시도
     */
    @Transactional
    override fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        return try {
            // Keycloak에서 토큰 쌍 받기
            val keycloakTokens = authenticateWithKeycloak(request.email, request.password)
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

            val keycloakAccessToken = keycloakTokens["access_token"] as? String
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            val keycloakRefreshToken = keycloakTokens["refresh_token"] as? String

            // Keycloak 액세스 토큰으로 사용자 정보 조회
            val userInfo = getUserInfoFromKeycloak(keycloakAccessToken)
            val email = userInfo["email"] as? String
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

            // 기존 사용자와 매핑 또는 자동 생성
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: createUserFromKeycloak(userInfo)

            publishLoginSuccessEvent(user, email, servletRequest)

            // 내부 JWT 액세스 토큰 생성 (기존 API 호환성 유지)
            val internalAccessToken = jwtTokenProvider.createAccessToken(user)

            // Keycloak 리프레시 토큰 저장 (remember me 여부와 관계없이)
            val finalRefreshToken = if (request.rememberMe && keycloakRefreshToken != null) {
                saveKeycloakRefreshToken(keycloakRefreshToken, user.email, servletRequest)
                keycloakRefreshToken
            } else {
                null
            }

            TokenPair(
                AccessTokenResponse.of(internalAccessToken, jwtProperties.accessTokenExpiresIn),
                finalRefreshToken
            )
        } catch (e: WebClientResponseException) {
            publishLoginFailureEvent(request.email, servletRequest, "HTTP ${e.statusCode}")
            logger.debug { "Keycloak 인증 실패 (HTTP ${e.statusCode}): ${request.email}" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (e: BusinessException) {
            publishLoginFailureEvent(request.email, servletRequest, e.message ?: "인증 실패")
            throw e
        } catch (e: Exception) {
            publishLoginFailureEvent(request.email, servletRequest, "시스템 오류")
            logger.error(e) { "Keycloak 인증 중 예상치 못한 오류: ${request.email}" }
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }
    }

    /**
     * Keycloak 리프레시 토큰으로 새로운 토큰 쌍 발급
     */
    @Transactional
    override fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        return try {
            // Redis에서 Keycloak 리프레시 토큰 정보 조회
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken)
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
            val email = tokenInfo["email"]
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)

            // Keycloak에서 토큰 갱신
            val newTokens = refreshKeycloakToken(refreshToken)
                ?: throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
            val newKeycloakRefreshToken = newTokens["refresh_token"] as? String ?: refreshToken

            // 사용자 정보 재조회 (최신 정보 반영)
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)

            // 새로운 내부 액세스 토큰 생성
            val newInternalAccessToken = jwtTokenProvider.createAccessToken(user)

            // 새로운 Keycloak 리프레시 토큰 저장
            if (newKeycloakRefreshToken != refreshToken) {
                deleteKeycloakRefreshToken(refreshToken)
                saveKeycloakRefreshToken(newKeycloakRefreshToken, email, servletRequest)
            }

            publishRefreshSuccessEvent(user, email, servletRequest)

            TokenPair(
                AccessTokenResponse.of(newInternalAccessToken, jwtProperties.accessTokenExpiresIn),
                newKeycloakRefreshToken
            )
        } catch (_: WebClientResponseException.Unauthorized) {
            deleteKeycloakRefreshToken(refreshToken)
            publishRefreshFailureEvent(servletRequest, "토큰 만료")
            throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        } catch (e: JwtAuthenticationException) {
            publishRefreshFailureEvent(servletRequest, "토큰 검증 실패")
            throw e
        } catch (e: Exception) {
            publishRefreshFailureEvent(servletRequest, "갱신 실패")
            logger.warn(e) { "Keycloak 토큰 갱신 실패: $refreshToken" }
            throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        }
    }

    /**
     * Keycloak 로그아웃 처리
     */
    override fun logout(refreshToken: String) {
        try {
            // Redis에서 토큰 정보 조회
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken) ?: return

            // Keycloak에서 로그아웃 (토큰 무효화)
            val formData = LinkedMultiValueMap<String, String>().apply {
                add("client_id", keycloakProperties.clientId)
                add("client_secret", keycloakProperties.clientSecret)
                add("refresh_token", refreshToken)
            }

            keycloakWebClient
                .post()
                .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/logout")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block()

            logger.debug { "Keycloak 로그아웃 성공: ${tokenInfo["email"]}" }
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 로그아웃 실패하지만 계속 진행: $refreshToken" }
        } finally {
            // Redis에서 토큰 정보 삭제
            deleteKeycloakRefreshToken(refreshToken)
        }
    }

    /**
     * Keycloak 우선 사용자 생성
     * Keycloak에 먼저 생성하고 로컬 DB에 동기화
     */
    @Transactional
    override fun createUser(request: UserCreateRequest): User {
        return try {
            // 1. 서비스 계정 토큰 획득
            val serviceToken = getServiceAccountToken()
                ?: throw BusinessException(AuthErrorCode.KEYCLOAK_SERVICE_ERROR)

            // 2. Keycloak에 사용자 생성
            val keycloakUserCreated = createKeycloakUser(serviceToken, request)
            if (!keycloakUserCreated) {
                throw BusinessException(AuthErrorCode.KEYCLOAK_USER_CREATE_FAILED)
            }

            // 3. 로컬 DB에 사용자 생성
            val user = userResourceCoordinator.createUser(request)

            logger.info { "Keycloak 우선 사용자 생성 완료: ${user.email}" }
            user
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error(e) { "Keycloak 사용자 생성 실패: ${request.email}" }
            throw BusinessException(AuthErrorCode.USER_CREATE_FAILED)
        }
    }

    /**
     * Keycloak에서 사용자 인증
     */
    private fun authenticateWithKeycloak(email: String, password: String): Map<String, Any>? {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "password")
            add("client_id", keycloakProperties.clientId)
            add("client_secret", keycloakProperties.clientSecret)
            add("username", email)
            add("password", password)
            add("scope", "openid profile email offline_access")
        }

        return try {
            keycloakWebClient
                .post()
                .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .timeout(Duration.ofSeconds(5))
                .block()
        } catch (e: Exception) {
            logger.debug { "Keycloak 인증 실패: ${e.message}" }
            null
        }
    }

    /**
     * Keycloak 리프레시 토큰으로 새로운 토큰 쌍 요청
     */
    private fun refreshKeycloakToken(refreshToken: String): Map<String, Any>? {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "refresh_token")
            add("client_id", keycloakProperties.clientId)
            add("client_secret", keycloakProperties.clientSecret)
            add("refresh_token", refreshToken)
        }

        return try {
            keycloakWebClient
                .post()
                .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .timeout(Duration.ofSeconds(5))
                .block()
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 토큰 갱신 실패" }
            null
        }
    }

    /**
     * Keycloak에서 사용자 정보 조회
     */
    private fun getUserInfoFromKeycloak(accessToken: String): Map<String, Any> {
        return keycloakWebClient
            .get()
            .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/userinfo")
            .headers { headers -> headers.setBearerAuth(accessToken) }
            .retrieve()
            .bodyToMono<Map<String, Any>>()
            .timeout(Duration.ofSeconds(5))
            .block() ?: throw RuntimeException("사용자 정보 조회 실패")
    }

    /**
     * Keycloak 사용자 정보로부터 새 User 엔티티 생성
     */
    private fun createUserFromKeycloak(userInfo: Map<String, Any>): User {
        val email = userInfo["email"] as? String
            ?: throw IllegalArgumentException("이메일 정보가 없습니다")
        val username = userInfo["preferred_username"] as? String ?: email.substringBefore("@")
        val firstName = userInfo["given_name"] as? String ?: ""
        val lastName = userInfo["family_name"] as? String ?: ""

        logger.info { "Keycloak에서 새 사용자 생성: $email" }

        return User.of(
            username = username,
            email = email,
            password = "", // Keycloak에서 관리되므로 빈 값
            firstName = firstName,
            lastName = lastName,
            isStaff = false,
            isSuperuser = false,
            isActive = true
        ).let { userRepository.save(it) }
    }

    /**
     * 서비스 계정 토큰 획득
     */
    private fun getServiceAccountToken(): String? {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "client_credentials")
            add("client_id", keycloakProperties.clientId)
            add("client_secret", keycloakProperties.clientSecret)
        }

        return try {
            keycloakWebClient
                .post()
                .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/token")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono<Map<String, Any>>()
                .timeout(Duration.ofSeconds(5))
                .block()
                ?.get("access_token") as? String
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 서비스 계정 토큰 획득 실패" }
            null
        }
    }

    /**
     * Keycloak에 새 사용자 생성
     */
    private fun createKeycloakUser(serviceToken: String, request: UserCreateRequest): Boolean {
        val userData = mapOf(
            "username" to request.email,
            "email" to request.email,
            "firstName" to request.firstName,
            "lastName" to request.lastName,
            "enabled" to true,
            "emailVerified" to true,
            "credentials" to listOf(
                mapOf(
                    "type" to "password",
                    "value" to request.password,
                    "temporary" to false
                )
            )
        )

        return try {
            keycloakWebClient
                .post()
                .uri("/admin/realms/${keycloakProperties.realm}/users")
                .headers { headers -> headers.setBearerAuth(serviceToken) }
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userData))
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block()

            logger.info { "Keycloak 사용자 생성 완료: ${request.email}" }
            true
        } catch (_: WebClientResponseException.Conflict) {
            logger.debug { "사용자가 이미 존재함: ${request.email}" }
            true
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 생성 실패: ${request.email}" }
            false
        }
    }

    // Redis 관련 헬퍼 메서드들 (기존과 동일)
    private fun saveKeycloakRefreshToken(
        keycloakRefreshToken: String,
        email: String,
        servletRequest: HttpServletRequest
    ) {
        val clientIp = IpUtils.getClientIp(servletRequest)

        with(redisTemplate) {
            opsForHash<String, String>().putAll(
                "${RedisKey.KEYCLOAK_REFRESH_PREFIX}$keycloakRefreshToken",
                mapOf(
                    "email" to email,
                    "ip" to clientIp,
                    "type" to "keycloak"
                )
            )

            expire(
                "${RedisKey.KEYCLOAK_REFRESH_PREFIX}$keycloakRefreshToken",
                jwtProperties.refreshTokenExpiresIn,
                TimeUnit.SECONDS
            )

            opsForValue().set(
                "${RedisKey.KEYCLOAK_EMAIL_PREFIX}:$email",
                keycloakRefreshToken,
                jwtProperties.refreshTokenExpiresIn,
                TimeUnit.SECONDS
            )
        }
    }

    private fun getKeycloakRefreshTokenInfo(refreshToken: String): Map<String, String>? {
        return try {
            val tokenInfo = redisTemplate.opsForHash<String, String>()
                .entries("${RedisKey.KEYCLOAK_REFRESH_PREFIX}$refreshToken")

            if (tokenInfo.isEmpty() || tokenInfo["type"] != "keycloak") {
                return null
            }

            tokenInfo
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 리프레시 토큰 정보 조회 실패: $refreshToken" }
            null
        }
    }

    private fun deleteKeycloakRefreshToken(refreshToken: String) {
        try {
            with(redisTemplate) {
                val email = opsForHash<String, String>()
                    .get("${RedisKey.KEYCLOAK_REFRESH_PREFIX}$refreshToken", "email")

                delete("${RedisKey.KEYCLOAK_REFRESH_PREFIX}$refreshToken")

                email?.let {
                    delete("${RedisKey.KEYCLOAK_EMAIL_PREFIX}:$it")
                }
            }
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 리프레시 토큰 삭제 실패: $refreshToken" }
        }
    }

    // 이벤트 발행 헬퍼 메서드들
    private fun publishLoginSuccessEvent(user: User, email: String, servletRequest: HttpServletRequest) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "Keycloak 전용 로그인: 성공",
            )
        )
    }

    private fun publishLoginFailureEvent(email: String, servletRequest: HttpServletRequest, reason: String) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = null,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = false,
                reason = "Keycloak 전용 로그인: 실패 - $reason",
            )
        )
    }

    private fun publishRefreshSuccessEvent(user: User, email: String, servletRequest: HttpServletRequest) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "Keycloak 전용 리프레시: 성공",
            )
        )
    }

    private fun publishRefreshFailureEvent(servletRequest: HttpServletRequest, reason: String) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = null,
                email = null,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = false,
                reason = "Keycloak 전용 리프레시: 실패 - $reason",
            )
        )
    }
}