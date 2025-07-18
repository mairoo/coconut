package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.domain.user.event.LoginEvent
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.repository.UserRepository
import kr.pincoin.api.domain.user.vo.TokenPair
import kr.pincoin.api.global.properties.JwtProperties
import kr.pincoin.api.global.properties.KeycloakProperties
import kr.pincoin.api.global.security.jwt.JwtTokenProvider
import kr.pincoin.api.global.utils.IpUtils
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
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
    private val keycloakWebClient: WebClient
) {
    private val logger = KotlinLogging.logger {}

    fun loginWithKeycloak(
        request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): TokenPair? {
        if (!keycloakProperties.enabled) {
            return null
        }

        return try {
            val keycloakToken = authenticateWithKeycloak(request.email, request.password)
                ?: return null

            // Keycloak 토큰으로 사용자 정보 조회
            val userInfo = getUserInfoFromKeycloak(keycloakToken)
            val email = userInfo["email"] as? String ?: return null

            // 기존 사용자와 매핑
            val user = userRepository.findUser(UserSearchCriteria(email = email, isActive = true))
                ?: if (keycloakProperties.userMigration.autoCreate) {
                    createUserFromKeycloak(userInfo)
                } else {
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

            if (request.rememberMe) {
                val refreshToken = jwtTokenProvider.createRefreshToken()
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
        } catch (e: Exception) {
            logger.error(e) { "Keycloak 인증 실패: ${request.email}" }

            eventPublisher.publishEvent(
                LoginEvent(
                    ipAddress = IpUtils.getClientIp(servletRequest),
                    userId = null,
                    email = request.email,
                    userAgent = servletRequest.getHeader("User-Agent"),
                    isSuccessful = false,
                    reason = "Keycloak 로그인: 실패 - ${e.message}",
                )
            )

            null
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
                    logger.debug { "Keycloak 인증 요청 실패: $error" }
                    Mono.empty()
                }
                .block()
                ?.get("access_token") as? String
        } catch (e: Exception) {
            logger.debug { "Keycloak 인증 실패: $e" }
            null
        }
    }

    private fun getUserInfoFromKeycloak(
        accessToken: String,
    ): Map<String, Any> = keycloakWebClient
        .get()
        .uri("/realms/${keycloakProperties.realm}/protocol/openid-connect/userinfo")
        .headers { headers ->
            headers.setBearerAuth(accessToken)
        }
        .retrieve()
        .bodyToMono<Map<String, Any>>()
        .timeout(Duration.ofSeconds(5))
        .block() ?: throw RuntimeException("사용자 정보 조회 실패")

    private fun createUserFromKeycloak(
        userInfo: Map<String, Any>,
    ): User {
        // userInfo에서 필요한 정보 추출
        val email = userInfo["email"] as String
        val username = userInfo["preferred_username"] as? String ?: email
        val firstName = userInfo["given_name"] as? String ?: ""
        val lastName = userInfo["family_name"] as? String ?: ""

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
}