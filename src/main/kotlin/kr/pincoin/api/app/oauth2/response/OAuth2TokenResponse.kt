package kr.pincoin.api.app.oauth2.response

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakTokenResponse

/**
 * OAuth2 토큰 응답
 *
 * Keycloak에서 토큰 교환 후 클라이언트에게 반환하는 응답
 */
data class OAuth2TokenResponse(
    /**
     * JWT Access Token
     */
    @JsonProperty("accessToken")
    val accessToken: String,

    /**
     * JWT Refresh Token
     */
    @JsonProperty("refreshToken")
    val refreshToken: String,

    /**
     * ID Token (사용자 정보 포함)
     */
    @JsonProperty("idToken")
    val idToken: String,

    /**
     * 토큰 타입 (보통 "Bearer")
     */
    @JsonProperty("tokenType")
    val tokenType: String = "Bearer",

    /**
     * Access Token 만료 시간 (초)
     */
    @JsonProperty("expiresIn")
    val expiresIn: Long,

    /**
     * 토큰 스코프
     */
    @JsonProperty("scope")
    val scope: String,
) {
    companion object {
        fun of(
            accessToken: String,
            refreshToken: String,
            idToken: String,
            expiresIn: Long,
            scope: String,
            tokenType: String = "Bearer",
        ): OAuth2TokenResponse =
            OAuth2TokenResponse(
                accessToken = accessToken,
                refreshToken = refreshToken,
                idToken = idToken,
                tokenType = tokenType,
                expiresIn = expiresIn,
                scope = scope,
            )

        fun from(
            keycloakResponse: KeycloakTokenResponse,
        ): OAuth2TokenResponse =
            OAuth2TokenResponse(
                accessToken = keycloakResponse.accessToken,
                refreshToken = keycloakResponse.refreshToken ?: "",
                idToken = keycloakResponse.idToken ?: "",
                expiresIn = keycloakResponse.expiresIn,
                scope = keycloakResponse.scope ?: "openid profile email",
                tokenType = keycloakResponse.tokenType,
            )
    }
}