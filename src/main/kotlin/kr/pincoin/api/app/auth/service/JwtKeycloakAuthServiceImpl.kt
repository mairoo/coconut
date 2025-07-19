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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration
import java.util.concurrent.TimeUnit

/**
 * JWT + Keycloak 하이브리드 인증 서비스 구현체 (이관 중)
 *
 * Keycloak 인증을 우선 시도하고, 실패 시 JWT 인증으로 폴백
 * 기존 사용자는 JWT로 로그인하며 백그라운드에서 Keycloak 동기화
 * 신규 사용자는 로컬 DB + Keycloak 동시 생성
 */
@Service
class JwtKeycloakAuthServiceImpl(
    private val userResourceCoordinator: UserResourceCoordinator,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val keycloakProperties: KeycloakProperties,
    private val redisTemplate: RedisTemplate<String, String>,
    private val eventPublisher: ApplicationEventPublisher,
    private val keycloakWebClient: WebClient,
) : AuthService {
    private val logger = KotlinLogging.logger {}

    /**
     * 하이브리드 로그인 처리
     * 1. Keycloak 인증 시도 (우선)
     * 2. 실패 시 JWT 인증 + Keycloak 백그라운드 동기화
     */
    @Transactional
    override fun login(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        // 1단계: Keycloak 인증 시도
        if (keycloakProperties.enabled) {
            loginWithKeycloak(request, servletRequest)?.let { tokenPair ->
                logger.debug { "Keycloak 로그인 성공: ${request.email}" }
                return tokenPair
            }
        }

        // 2단계: JWT 인증 (폴백) + Keycloak 동기화
        logger.debug { "Keycloak 인증 실패 또는 비활성화, JWT 인증 시도: ${request.email}" }
        return loginWithJwt(request, servletRequest)
    }

    /**
     * 하이브리드 리프레시 토큰 처리
     * Keycloak 리프레시 토큰 우선, 실패 시 JWT 리프레시 토큰 처리
     */
    @Transactional
    override fun refresh(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        // 1단계: Keycloak 리프레시 토큰으로 처리 시도
        if (keycloakProperties.enabled) {
            refreshWithKeycloak(refreshToken, servletRequest)?.let { tokenPair ->
                logger.debug { "Keycloak 리프레시 성공: $refreshToken" }
                return tokenPair
            }
        }

        // 2단계: JWT 리프레시 토큰 처리 (폴백)
        logger.debug { "Keycloak 리프레시 실패 또는 비활성화, JWT 리프레시 시도: $refreshToken" }
        return refreshWithJwt(refreshToken, servletRequest)
    }

    /**
     * 하이브리드 로그아웃 처리
     * Keycloak과 JWT 리프레시 토큰 모두 정리
     */
    override fun logout(refreshToken: String) {
        // Keycloak 로그아웃 시도
        if (keycloakProperties.enabled) {
            try {
                logoutFromKeycloak(refreshToken)
            } catch (e: Exception) {
                logger.warn(e) { "Keycloak 로그아웃 실패하지만 계속 진행" }
            }
        }

        // JWT 리프레시 토큰 삭제
        logoutJwt(refreshToken)

        logger.debug { "하이브리드 로그아웃 완료: $refreshToken" }
    }

    /**
     * 하이브리드 사용자 생성
     * 로컬 DB 생성 후 Keycloak 백그라운드 동기화
     */
    @Transactional
    override fun createUser(request: UserCreateRequest): User {
        // 1. 기존 방식으로 사용자 생성 (로컬 DB)
        val user = userResourceCoordinator.createUser(request)

        // 2. Keycloak에 사용자 동기화 (백그라운드)
        if (keycloakProperties.enabled) {
            try {
                val syncResult = syncUserToKeycloak(user, request.password)
                if (syncResult) {
                    logger.info { "신규 사용자 Keycloak 동기화 성공: ${user.email}" }
                } else {
                    logger.debug { "Keycloak 동기화 스킵 (설정 비활성화): ${user.email}" }
                }
            } catch (e: Exception) {
                // 동기화 실패해도 회원가입은 성공으로 처리
                logger.warn(e) { "신규 사용자 Keycloak 동기화 실패하지만 회원가입은 성공: ${user.email}" }
            }
        }

        return user
    }

    /**
     * Keycloak 로그인 처리
     */
    private fun loginWithKeycloak(
        request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        return try {
            // Keycloak에서 토큰 쌍 받기
            val keycloakTokens = authenticateWithKeycloak(request.email, request.password)
                ?: return null

            val keycloakAccessToken = keycloakTokens["access_token"] as? String ?: return null
            val keycloakRefreshToken = keycloakTokens["refresh_token"] as? String

            // Keycloak 액세스 토큰으로 사용자 정보 조회
            val userInfo = getUserInfoFromKeycloak(keycloakAccessToken)
            val email = userInfo["email"] as? String ?: return null

            // 기존 사용자와 매핑
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: if (keycloakProperties.userMigration.autoCreate) {
                    logger.info { "새 사용자 자동 생성: $email" }
                    createUserFromKeycloak(userInfo)
                } else {
                    logger.warn { "사용자를 찾을 수 없고 자동 생성이 비활성화됨: $email" }
                    return null
                }

            publishLoginSuccessEvent(user, email, servletRequest, "Keycloak")

            // 내부 JWT 액세스 토큰 생성
            val internalAccessToken = jwtTokenProvider.createAccessToken(user)

            // Keycloak 리프레시 토큰 저장
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
            logger.debug { "Keycloak 인증 실패 (HTTP ${e.statusCode}): ${request.email}" }
            null
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 인증 중 예외: ${request.email}" }
            null
        }
    }

    /**
     * JWT 로그인 처리 (폴백)
     */
    private fun loginWithJwt(request: SignInRequest, servletRequest: HttpServletRequest): TokenPair {
        val user = try {
            userRepository.findUser(UserSearchCriteria(email = request.email, isActive = true))
                ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        } catch (_: Exception) {
            publishLoginFailureEvent(request.email, servletRequest, "사용자 찾을 수 없음", "JWT")
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        if (!passwordEncoder.matches(request.password, user.password)) {
            publishLoginFailureEvent(request.email, servletRequest, "비밀번호 불일치", "JWT")
            throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
        }

        publishLoginSuccessEvent(user, request.email, servletRequest, "JWT")

        // 🆕 로그인 성공 후 Keycloak 동기화 시도 (백그라운드)
        if (keycloakProperties.enabled) {
            try {
                val syncResult = syncUserToKeycloak(user, request.password)
                if (syncResult) {
                    logger.info { "기존 사용자 Keycloak 동기화 성공: ${user.email}" }
                } else {
                    logger.debug { "Keycloak 동기화 스킵: ${user.email}" }
                }
            } catch (e: Exception) {
                logger.warn(e) { "기존 사용자 Keycloak 동기화 실패: ${user.email}" }
            }
        }

        val accessToken = jwtTokenProvider.createAccessToken(user)

        // JWT 리프레시 토큰 발급
        return if (request.rememberMe) {
            // 기존 리프레시 토큰 삭제
            with(redisTemplate) {
                opsForValue().get(user.email)?.let { oldRefreshToken ->
                    delete(oldRefreshToken)
                }
            }

            val refreshToken = jwtTokenProvider.createRefreshToken()
            saveJwtRefreshTokenInfo(refreshToken, user.email, servletRequest)

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
     * Keycloak 리프레시 토큰 처리
     */
    private fun refreshWithKeycloak(
        refreshToken: String,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        return try {
            // Redis에서 Keycloak 리프레시 토큰 정보 조회
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken) ?: return null
            val email = tokenInfo["email"] ?: return null

            // Keycloak에서 토큰 갱신
            val newTokens = refreshKeycloakToken(refreshToken) ?: return null
            val newKeycloakRefreshToken = newTokens["refresh_token"] as? String ?: refreshToken

            // 사용자 정보 재조회
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: return null

            // 새로운 내부 액세스 토큰 생성
            val newInternalAccessToken = jwtTokenProvider.createAccessToken(user)

            // 새로운 Keycloak 리프레시 토큰 저장
            if (newKeycloakRefreshToken != refreshToken) {
                deleteKeycloakRefreshToken(refreshToken)
                saveKeycloakRefreshToken(newKeycloakRefreshToken, email, servletRequest)
            }

            publishRefreshSuccessEvent(user, email, servletRequest, "Keycloak")

            TokenPair(
                AccessTokenResponse.of(newInternalAccessToken, jwtProperties.accessTokenExpiresIn),
                newKeycloakRefreshToken
            )
        } catch (_: WebClientResponseException.Unauthorized) {
            logger.debug { "Keycloak 리프레시 토큰 만료: $refreshToken" }
            deleteKeycloakRefreshToken(refreshToken)
            null
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 토큰 갱신 실패: $refreshToken" }
            null
        }
    }

    /**
     * JWT 리프레시 토큰 처리 (폴백)
     */
    private fun refreshWithJwt(refreshToken: String, servletRequest: HttpServletRequest): TokenPair {
        try {
            validateJwtRefreshToken(refreshToken, servletRequest)
        } catch (e: JwtAuthenticationException) {
            publishRefreshFailureEvent(servletRequest, "토큰 검증 실패", "JWT")
            throw e
        }

        with(redisTemplate) {
            val email = opsForHash<String, String>()
                .get(refreshToken, RedisKey.EMAIL)
                ?: run {
                    publishRefreshFailureEvent(servletRequest, "이메일 검증 실패", "JWT")
                    throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
                }

            val user = try {
                userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                    ?: throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            } catch (_: Exception) {
                publishRefreshFailureEvent(servletRequest, "사용자 없음", "JWT", email)
                throw BusinessException(AuthErrorCode.INVALID_CREDENTIALS)
            }

            publishRefreshSuccessEvent(user, email, servletRequest, "JWT")

            val newAccessToken = jwtTokenProvider.createAccessToken(user)
            val newRefreshToken = jwtTokenProvider.createRefreshToken()

            try {
                delete(refreshToken)
                saveJwtRefreshTokenInfo(newRefreshToken, email, servletRequest)
            } catch (e: Exception) {
                publishRefreshFailureEvent(servletRequest, "갱신 오류", "JWT", email, user.id)
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
     * 기존 사용자 Keycloak 동기화
     */
    private fun syncUserToKeycloak(user: User, plainPassword: String? = null): Boolean {
        if (!keycloakProperties.userMigration.autoCreate) {
            return false
        }

        return try {
            val serviceToken = getServiceAccountToken() ?: return false

            if (checkUserExists(serviceToken, user.email)) {
                logger.debug { "사용자가 이미 Keycloak에 존재함: ${user.email}" }
                return true
            }

            val created = createKeycloakUser(serviceToken, user, plainPassword)
            if (created) {
                logger.info { "Keycloak에 사용자 생성 완료: ${user.email}" }
            }

            created
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 동기화 실패: ${user.email}" }
            false
        }
    }

    // === Keycloak API 메서드들 ===
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
                .onErrorResume { Mono.empty() }
                .block()
        } catch (e: Exception) {
            logger.debug { "Keycloak 인증 중 예외: ${e.message}" }
            null
        }
    }

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
                .onErrorResume { Mono.empty() }
                .block()
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 토큰 갱신 중 예외" }
            null
        }
    }

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

    private fun createUserFromKeycloak(userInfo: Map<String, Any>): User {
        val email = userInfo["email"] as? String
            ?: throw IllegalArgumentException("이메일 정보가 없습니다")
        val username = userInfo["preferred_username"] as? String ?: email.substringBefore("@")
        val firstName = userInfo["given_name"] as? String ?: ""
        val lastName = userInfo["family_name"] as? String ?: ""

        return User.of(
            username = username,
            email = email,
            password = "",
            firstName = firstName,
            lastName = lastName,
            isStaff = false,
            isSuperuser = false,
            isActive = true
        ).let { userRepository.save(it) }
    }

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
                .onErrorResume { Mono.empty() }
                .block()
                ?.get("access_token") as? String
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 서비스 계정 토큰 획득 실패" }
            null
        }
    }

    private fun checkUserExists(serviceToken: String, email: String): Boolean {
        return try {
            val response = keycloakWebClient
                .get()
                .uri("/admin/realms/${keycloakProperties.realm}/users?email={email}", email)
                .headers { headers -> headers.setBearerAuth(serviceToken) }
                .retrieve()
                .bodyToMono<List<Map<String, Any>>>()
                .timeout(Duration.ofSeconds(5))
                .block()

            response?.isNotEmpty() == true
        } catch (e: Exception) {
            logger.debug(e) { "사용자 존재 확인 실패: $email" }
            false
        }
    }

    private fun createKeycloakUser(serviceToken: String, user: User, plainPassword: String? = null): Boolean {
        val userData = if (plainPassword != null) {
            mapOf(
                "username" to user.email,
                "email" to user.email,
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "enabled" to true,
                "emailVerified" to true,
                "credentials" to listOf(
                    mapOf(
                        "type" to "password",
                        "value" to plainPassword,
                        "temporary" to false
                    )
                )
            )
        } else {
            mapOf(
                "username" to user.email,
                "email" to user.email,
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "enabled" to true,
                "emailVerified" to true,
                "requiredActions" to listOf("UPDATE_PASSWORD")
            )
        }

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

            true
        } catch (_: WebClientResponseException.Conflict) {
            logger.debug { "사용자가 이미 존재함: ${user.email}" }
            true
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 생성 실패: ${user.email}" }
            false
        }
    }

    private fun logoutFromKeycloak(refreshToken: String) {
        try {
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken) ?: return

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
            logger.warn(e) { "Keycloak 로그아웃 실패: $refreshToken" }
        } finally {
            deleteKeycloakRefreshToken(refreshToken)
        }
    }

    // === Redis 관련 메서드들 ===
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

    private fun saveJwtRefreshTokenInfo(
        refreshToken: String,
        email: String,
        request: HttpServletRequest
    ) {
        val clientIp = IpUtils.getClientIp(request)

        with(redisTemplate) {
            opsForHash<String, String>()
                .putAll(
                    refreshToken,
                    mapOf(RedisKey.EMAIL to email, RedisKey.IP_ADDRESS to clientIp)
                )

            expire(refreshToken, jwtProperties.refreshTokenExpiresIn, TimeUnit.SECONDS)

            opsForValue().set(
                email,
                refreshToken,
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

    private fun validateJwtRefreshToken(refreshToken: String, request: HttpServletRequest) {
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

    private fun logoutJwt(refreshToken: String) {
        with(redisTemplate) {
            val email = opsForHash<String, String>().get(refreshToken, RedisKey.EMAIL) ?: return

            delete(refreshToken)
            delete(email)
        }
    }

    // === 이벤트 발행 메서드들 ===
    private fun publishLoginSuccessEvent(
        user: User,
        email: String,
        servletRequest: HttpServletRequest,
        authType: String
    ) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "하이브리드 로그인 ($authType): 성공",
            )
        )
    }

    private fun publishLoginFailureEvent(
        email: String,
        servletRequest: HttpServletRequest,
        reason: String,
        authType: String
    ) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = null,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = false,
                reason = "하이브리드 로그인 ($authType): 실패 - $reason",
            )
        )
    }

    private fun publishRefreshSuccessEvent(
        user: User,
        email: String,
        servletRequest: HttpServletRequest,
        authType: String
    ) {
        eventPublisher.publishEvent(
            LoginEvent(
                ipAddress = IpUtils.getClientIp(servletRequest),
                userId = user.id,
                email = email,
                userAgent = servletRequest.getHeader("User-Agent"),
                isSuccessful = true,
                reason = "하이브리드 리프레시 ($authType): 성공",
            )
        )
    }

    private fun publishRefreshFailureEvent(
        servletRequest: HttpServletRequest,
        reason: String,
        authType: String,
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
                reason = "하이브리드 리프레시 ($authType): 실패 - $reason",
            )
        )
    }

    companion object {
        private const val UUID_PATTERN =
            "^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$"
    }
}