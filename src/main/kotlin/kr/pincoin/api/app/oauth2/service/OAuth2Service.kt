package kr.pincoin.api.app.oauth2.service

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.oauth2.response.OAuth2LoginUrlResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.global.utils.ClientUtils
import kr.pincoin.api.global.utils.OAuth2Utils
import org.springframework.stereotype.Service

/**
 * OAuth2 Authorization Code Flow 서비스
 *
 * OAuth2 로그인 URL 생성을 담당합니다.
 * OAuth2Utils를 활용하여 간결하고 재사용 가능한 구조를 제공합니다.
 */
@Service
class OAuth2Service(
    private val keycloakProperties: KeycloakProperties,
) {
    /**
     * OAuth2 로그인 URL 생성
     *
     * 클라이언트가 제공한 redirect_uri를 검증한 후
     * Keycloak Authorization 서버로의 로그인 URL을 생성합니다.
     *
     * @param redirectUri 로그인 성공 후 리다이렉트될 URI
     * @param httpServletRequest HTTP 요청 정보 (로깅 및 보안 검증용)
     * @return OAuth2 로그인 URL과 state 정보
     */
    fun generateLoginUrl(
        redirectUri: String,
        httpServletRequest: HttpServletRequest,
    ): OAuth2LoginUrlResponse {
        val clientInfo = ClientUtils.getClientInfo(httpServletRequest)

        // redirect_uri 보안 검증
        OAuth2Utils.validateRedirectUri(
            redirectUri = redirectUri,
            allowedUris = keycloakProperties.allowedRedirectUris,
            clientInfo = clientInfo,
        )

        // CSRF 방어용 state 생성
        val state = OAuth2Utils.generateSecureState()

        // Keycloak authorization URL 구성
        val baseUrl = "${keycloakProperties.serverUrl}/realms/${keycloakProperties.realm}/protocol/openid-connect/auth"
        val loginUrl = OAuth2Utils.buildAuthorizationUrl(
            baseUrl = baseUrl,
            clientId = keycloakProperties.clientId,
            redirectUri = redirectUri,
            state = state,
        )

        return OAuth2LoginUrlResponse.of(loginUrl, state)
    }
}