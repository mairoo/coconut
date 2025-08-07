package kr.pincoin.api.external.auth.keycloak.api.response

import com.fasterxml.jackson.annotation.JsonAlias
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 토큰 관련 데이터
 */
data class KeycloakTokenResponse(
    @field:JsonProperty("access_token")
    val accessToken: String,

    @field:JsonProperty("expires_in")
    val expiresIn: Long,

    @field:JsonProperty("refresh_expires_in")
    val refreshExpiresIn: Long,

    @field:JsonProperty("refresh_token")
    val refreshToken: String? = null,

    @field:JsonProperty("token_type")
    val tokenType: String,

    @field:JsonProperty("id_token")
    val idToken: String? = null,

    @field:JsonProperty("not_before_policy")
    @field:JsonAlias("not-before-policy")
    val notBeforePolicy: Long? = null,

    @field:JsonProperty("session_state")
    val sessionState: String? = null,

    @field:JsonProperty("scope")
    val scope: String? = null,
)