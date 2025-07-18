package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.domain.user.vo.TokenPair
import kr.pincoin.api.global.constant.RedisKey
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
import java.util.concurrent.TimeUnit

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

    fun loginWithKeycloak(
        request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        if (!keycloakProperties.enabled) {
            logger.debug { "Keycloak이 비활성화되어 있습니다" }
            return null
        }

        return try {
            val keycloakToken = authenticateWithKeycloak(request.email, request.password)
                ?: return null

            // Keycloak 토큰으로 사용자 정보 조회
            val userInfo = getUserInfoFromKeycloak(keycloakToken)
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

            // 내부 JWT 토큰 생성
            val accessToken = jwtTokenProvider.createAccessToken(user)

            // 리프레시 토큰 처리
            if (request.rememberMe) {
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
     * 기존 사용자 로그인 성공 시 Keycloak에 동기화
     */
    fun syncUserToKeycloak(user: User): Boolean {
        if (!keycloakProperties.enabled || !keycloakProperties.userMigration.autoCreate) {
            return false
        }

        return try {
            // 1. 관리자 토큰 획득
            val adminToken = getAdminToken() ?: return false

            // 2. 사용자가 이미 존재하는지 확인
            if (checkUserExists(adminToken, user.email)) {
                logger.debug { "사용자가 이미 Keycloak에 존재함: ${user.email}" }
                return true
            }

            // 3. 새 사용자 생성
            val created = createKeycloakUser(adminToken, user)
            if (created) {
                logger.info { "Keycloak에 사용자 생성 완료: ${user.email}" }
            }

            created
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 동기화 실패: ${user.email}" }
            false
        }
    }

    private fun authenticateWithKeycloak(
        email: String,
        password: String,
    ): String? {
        val formData = LinkedMultiValueMap<String, String>().apply {
            add("grant_type", "password")
            add("client_id", keycloakProperties.clientId)
            add("client_secret", keycloakProperties.clientSecret)
            add("username", email)
            add("password", password)
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
                ?.get("access_token") as? String
        } catch (e: Exception) {
            logger.debug { "Keycloak 인증 중 예외 발생: ${e.message}" }
            null
        }
    }

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

    private fun getAdminToken(): String? {
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
            logger.warn(e) { "Keycloak 관리자 토큰 획득 실패" }
            null
        }
    }

    private fun checkUserExists(adminToken: String, email: String): Boolean {
        return try {
            val response = keycloakWebClient
                .get()
                .uri("/admin/realms/${keycloakProperties.realm}/users?email={email}", email)
                .headers { headers ->
                    headers.setBearerAuth(adminToken)
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

    private fun createKeycloakUser(adminToken: String, user: User): Boolean {
        val userData = mapOf(
            "username" to user.email,
            "email" to user.email,
            "firstName" to user.firstName,
            "lastName" to user.lastName,
            "enabled" to true,
            "emailVerified" to true,
            "credentials" to listOf(
                mapOf(
                    "type" to "password",
                    "value" to "temporary123!", // 임시 비밀번호
                    "temporary" to true // 첫 로그인 시 변경 강제
                )
            )
        )

        return try {
            keycloakWebClient
                .post()
                .uri("/admin/realms/${keycloakProperties.realm}/users")
                .headers { headers ->
                    headers.setBearerAuth(adminToken)
                }
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(userData))
                .retrieve()
                .toBodilessEntity()
                .timeout(Duration.ofSeconds(5))
                .block()

            true
        } catch (e: WebClientResponseException.Conflict) {
            logger.debug { "사용자가 이미 존재함: ${user.email}" }
            true // 이미 존재하면 성공으로 간주
        } catch (e: Exception) {
            logger.warn(e) { "Keycloak 사용자 생성 실패: ${user.email}" }
            false
        }
    }

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

    private fun saveRefreshTokenInfo(
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
}