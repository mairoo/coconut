package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.domain.user.error.AuthErrorCode
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.domain.user.vo.TokenPair
import kr.pincoin.api.global.constant.RedisKey
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
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.bodyToMono
import reactor.core.publisher.Mono
import java.time.Duration

@Service
class KeycloakAuthService(
    private val keycloakProperties: KeycloakProperties,
    private val jwtTokenProvider: JwtTokenProvider,
    private val jwtProperties: JwtProperties,
    private val userRepository: UserRepository,
    private val eventPublisher: ApplicationEventPublisher,
    private val keycloakWebClient: WebClient,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * Keycloak 로그인 처리
     * 성공 시 Keycloak 리프레시 토큰과 내부 액세스 토큰 반환
     */
    fun loginWithKeycloak(
        request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        if (!keycloakProperties.enabled) {
            logger.debug { "Keycloak이 비활성화되어 있습니다" }
            return null
        }

        return try {
            // 🔄 Keycloak에서 토큰 쌍 받기 (액세스 + 리프레시)
            val keycloakTokens = authenticateWithKeycloak(request.email, request.password)
                ?: return null

            val keycloakAccessToken = keycloakTokens["access_token"] as? String ?: return null
            val keycloakRefreshToken = keycloakTokens["refresh_token"] as? String

            // Keycloak 액세스 토큰으로 사용자 정보 조회
            val userInfo = getUserInfoFromKeycloak(keycloakAccessToken)
            val email = userInfo["email"] as? String ?: run {
                logger.warn { "Keycloak 사용자 정보에서 이메일을 찾을 수 없습니다" }
                return null
            }

            // 기존 사용자와 매핑
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: if (keycloakProperties.userMigration.autoCreate) {
                    logger.info { "새 사용자 자동 생성: $email" }
                    createUserFromKeycloak(userInfo)
                } else {
                    logger.warn { "사용자를 찾을 수 없고 자동 생성이 비활성화됨: $email" }
                    return null
                }

            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = user.id,
                    email = email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = true,
                    reason = "Keycloak 로그인: 성공",
                )
            )

            // 내부 JWT 액세스 토큰 생성 (기존 API 호환성 유지)
            val internalAccessToken = jwtTokenProvider.createAccessToken(user)

            // 🆕 Keycloak 리프레시 토큰 저장 (remember me 여부와 관계없이)
            val finalRefreshToken = if (request.rememberMe && keycloakRefreshToken != null) {
                // Keycloak 리프레시 토큰을 Redis에 저장하여 추후 갱신에 사용
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
            publishLoginFailureEvent(request.email, servletRequest, "HTTP ${e.statusCode}")
            null
        } catch (e: Exception) {
            logger.error(e) { "Keycloak 인증 중 예상치 못한 오류: ${request.email}" }
            publishLoginFailureEvent(request.email, servletRequest, e.message ?: "알 수 없는 오류")
            null
        }
    }

    /**
     * 🆕 Keycloak 리프레시 토큰으로 새로운 토큰 쌍 발급
     */
    fun refreshWithKeycloak(
        refreshToken: String,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        if (!keycloakProperties.enabled) {
            return null
        }

        return try {
            // 1. Redis에서 Keycloak 리프레시 토큰 정보 조회
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken) ?: return null
            val email = tokenInfo["email"] ?: return null

            // 2. Keycloak에서 토큰 갱신
            val newTokens = refreshKeycloakToken(refreshToken) ?: return null
            val newKeycloakRefreshToken = newTokens["refresh_token"] as? String ?: refreshToken

            // 3. 사용자 정보 재조회 (최신 정보 반영)
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: run {
                    logger.warn { "리프레시 중 사용자를 찾을 수 없음: $email" }
                    return null
                }

            // 4. 새로운 내부 액세스 토큰 생성
            val newInternalAccessToken = jwtTokenProvider.createAccessToken(user)

            // 5. 새로운 Keycloak 리프레시 토큰 저장
            if (newKeycloakRefreshToken != refreshToken) {
                // 기존 토큰 삭제
                deleteKeycloakRefreshToken(refreshToken)
                // 새 토큰 저장
                saveKeycloakRefreshToken(newKeycloakRefreshToken, email, servletRequest)
            }

            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = user.id,
                    email = email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = true,
                    reason = "Keycloak 리프레시: 성공",
                )
            )

            TokenPair(
                AccessTokenResponse.of(newInternalAccessToken, jwtProperties.accessTokenExpiresIn),
                newKeycloakRefreshToken
            )
        } catch (_: WebClientResponseException.Unauthorized) {
            logger.debug { "Keycloak 리프레시 토큰 만료: $refreshToken" }
            deleteKeycloakRefreshToken(refreshToken)
            throw JwtAuthenticationException(AuthErrorCode.INVALID_REFRESH_TOKEN)
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 토큰 갱신 실패: $refreshToken" }
            null
        }
    }

    /**
     * 🆕 Keycloak 로그아웃 처리
     */
    fun logoutFromKeycloak(refreshToken: String) {
        if (!keycloakProperties.enabled) {
            return
        }

        try {
            // 1. Redis에서 토큰 정보 조회
            val tokenInfo = getKeycloakRefreshTokenInfo(refreshToken) ?: return

            // 2. Keycloak에서 로그아웃 (토큰 무효화)
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
     * 기존 사용자 로그인 성공 시 Keycloak에 동기화
     */
    fun syncUserToKeycloak(user: User, plainPassword: String? = null): Boolean {
        if (!keycloakProperties.enabled || !keycloakProperties.userMigration.autoCreate) {
            return false
        }

        return try {
            // 1. 서비스 계정 토큰 획득
            val serviceToken = getServiceAccountToken() ?: return false

            // 2. 사용자가 이미 존재하는지 확인
            if (checkUserExists(serviceToken, user.email)) {
                logger.debug { "사용자가 이미 Keycloak에 존재함: ${user.email}" }
                return true
            }

            // 3. 새 사용자 생성 (실제 비밀번호와 함께)
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

    /**
     * Keycloak 리프레시 토큰을 Redis에 저장
     */
    private fun saveKeycloakRefreshToken(
        keycloakRefreshToken: String,
        email: String,
        servletRequest: HttpServletRequest
    ) {
        val clientIp = IpUtils.getClientIp(servletRequest)

        with(redisTemplate) {
            // Keycloak 리프레시 토큰 정보 저장
            opsForHash<String, String>().putAll(
                "${RedisKey.KEYCLOAK_REFRESH_PREFIX}$keycloakRefreshToken",
                mapOf(
                    "email" to email,
                    "ip" to clientIp,
                    "type" to "keycloak"
                )
            )

            // 만료 시간 설정 (Keycloak 기본 리프레시 토큰 수명 사용)
            expire(
                "${RedisKey.KEYCLOAK_REFRESH_PREFIX}$keycloakRefreshToken",
                jwtProperties.refreshTokenExpiresIn,
                java.util.concurrent.TimeUnit.SECONDS
            )

            // 이메일로 역방향 조회 가능하도록 저장
            opsForValue().set(
                "${RedisKey.KEYCLOAK_EMAIL_PREFIX}:$email",
                keycloakRefreshToken,
                jwtProperties.refreshTokenExpiresIn,
                java.util.concurrent.TimeUnit.SECONDS
            )
        }
    }

    /**
     * Redis에서 Keycloak 리프레시 토큰 정보 조회
     */
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

    /**
     * Redis에서 Keycloak 리프레시 토큰 삭제
     */
    private fun deleteKeycloakRefreshToken(refreshToken: String) {
        try {
            with(redisTemplate) {
                // 토큰 정보에서 이메일 조회
                val email = opsForHash<String, String>()
                    .get("${RedisKey.KEYCLOAK_REFRESH_PREFIX}$refreshToken", "email")

                // 토큰 정보 삭제
                delete("${RedisKey.KEYCLOAK_REFRESH_PREFIX}$refreshToken")

                // 이메일 역방향 조회 키 삭제
                email?.let {
                    delete("${RedisKey.KEYCLOAK_EMAIL_PREFIX}:$it")
                }
            }
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 리프레시 토큰 삭제 실패: $refreshToken" }
        }
    }

    /**
     * Keycloak에서 사용자 인증을 시도합니다.
     * Resource Owner Password Credentials Grant 사용
     * 액세스 토큰과 리프레시 토큰 모두 반환
     */
    private fun authenticateWithKeycloak(
        email: String,
        password: String,
    ): Map<String, Any>? {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "password")
            add("client_id", keycloakProperties.clientId)
            add("client_secret", keycloakProperties.clientSecret)
            add("username", email)
            add("password", password)
            add("scope", "openid profile email offline_access") // 🆕 offline_access 추가로 리프레시 토큰 요청
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
                .onErrorResume { error ->
                    when (error) {
                        is WebClientResponseException.Unauthorized -> {
                            logger.debug { "Keycloak 인증 실패: 잘못된 자격증명" }
                        }

                        is WebClientResponseException -> {
                            logger.warn { "Keycloak 인증 실패 (HTTP ${error.statusCode}): ${error.responseBodyAsString}" }
                        }

                        else -> {
                            logger.warn { "Keycloak 인증 요청 실패: ${error.message}" }
                        }
                    }
                    Mono.empty()
                }
                .block()
        } catch (e: Exception) {
            logger.debug { "Keycloak 인증 중 예외 발생: ${e.message}" }
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
                .onErrorResume { error ->
                    when (error) {
                        is WebClientResponseException.Unauthorized -> {
                            logger.debug { "Keycloak 리프레시 토큰 만료 또는 무효" }
                        }

                        is WebClientResponseException -> {
                            logger.warn { "Keycloak 토큰 갱신 실패 (HTTP ${error.statusCode}): ${error.responseBodyAsString}" }
                        }

                        else -> {
                            logger.warn { "Keycloak 토큰 갱신 요청 실패: ${error.message}" }
                        }
                    }
                    Mono.empty()
                }
                .block()
        } catch (e: Exception) {
            logger.debug(e) { "Keycloak 토큰 갱신 중 예외 발생" }
            null
        }
    }

    /**
     * Keycloak에서 사용자 정보를 조회합니다.
     */
    private fun getUserInfoFromKeycloak(accessToken: String): Map<String, Any> {
        return keycloakWebClient
            .get()
            .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/userinfo")
            .headers { headers ->
                headers.setBearerAuth(accessToken)
            }
            .retrieve()
            .bodyToMono<Map<String, Any>>()
            .timeout(Duration.ofSeconds(5))
            .onErrorMap { error ->
                logger.error { "Keycloak 사용자 정보 조회 실패: ${error.message}" }
                RuntimeException("사용자 정보 조회 실패: ${error.message}", error)
            }
            .block() ?: throw RuntimeException("사용자 정보 조회 결과가 null입니다")
    }

    /**
     * Keycloak 사용자 정보로부터 새 User 엔티티를 생성합니다.
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
     * 서비스 계정 토큰을 획득합니다.
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
                .onErrorResume { error ->
                    when (error) {
                        is WebClientResponseException.Unauthorized -> {
                            logger.warn { "서비스 계정 토큰 획득 실패: 클라이언트 인증 실패" }
                        }

                        is WebClientResponseException -> {
                            logger.warn { "서비스 계정 토큰 획득 실패 (HTTP ${error.statusCode}): ${error.responseBodyAsString}" }
                        }

                        else -> {
                            logger.warn { "서비스 계정 토큰 요청 실패: ${error.message}" }
                        }
                    }
                    Mono.empty()
                }
                .block()
                ?.get("access_token") as? String
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 서비스 계정 토큰 획득 실패" }
            null
        }
    }

    /**
     * Keycloak에서 사용자가 이미 존재하는지 확인합니다.
     */
    private fun checkUserExists(serviceToken: String, email: String): Boolean {
        return try {
            val response = keycloakWebClient
                .get()
                .uri("/admin/realms/${keycloakProperties.realm}/users?email={email}", email)
                .headers { headers ->
                    headers.setBearerAuth(serviceToken)
                }
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

    /**
     * Keycloak에 새 사용자를 생성합니다.
     */
    private fun createKeycloakUser(serviceToken: String, user: User, plainPassword: String? = null): Boolean {
        val userData = if (plainPassword != null) {
            // 실제 비밀번호가 있는 경우 (로그인 성공 시)
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
                        "value" to plainPassword, // 사용자가 입력한 실제 비밀번호
                        "temporary" to false // 임시 비밀번호가 아님
                    )
                )
            )
        } else {
            // 비밀번호 없이 생성하는 경우
            mapOf(
                "username" to user.email,
                "email" to user.email,
                "firstName" to user.firstName,
                "lastName" to user.lastName,
                "enabled" to true,
                "emailVerified" to true,
                "requiredActions" to listOf("UPDATE_PASSWORD") // 비밀번호 업데이트 요구
            )
        }

        return try {
            keycloakWebClient
                .post()
                .uri("/admin/realms/${keycloakProperties.realm}/users")
                .headers { headers ->
                    headers.setBearerAuth(serviceToken)
                }
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userData))
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block()

            if (plainPassword != null) {
                logger.info { "Keycloak 사용자 생성 완료 (실제 비밀번호 포함): ${user.email}" }
            } else {
                logger.info { "Keycloak 사용자 생성 완료 (비밀번호 재설정 필요): ${user.email}" }
            }
            true
        } catch (_: WebClientResponseException.Conflict) {
            logger.debug { "사용자가 이미 존재함: ${user.email}" }
            true
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 생성 실패: ${user.email}" }
            false
        }
    }

    /**
     * 로그인 실패 이벤트를 발행합니다.
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
                reason = "Keycloak 로그인: 실패 - $reason",
            )
        )
    }
}