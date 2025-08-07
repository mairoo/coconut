package kr.pincoin.api.external.auth.keycloak.api.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * UserInfo 데이터
 */
data class KeycloakUserInfoResponse(
    @field:JsonProperty("sub")
    val sub: String,

    @field:JsonProperty("email_verified")
    val emailVerified: Boolean,

    @field:JsonProperty("preferred_username")
    val preferredUsername: String,

    @field:JsonProperty("name")
    val name: String? = null,

    @field:JsonProperty("given_name")
    val givenName: String? = null,

    @field:JsonProperty("family_name")
    val familyName: String? = null,

    @field:JsonProperty("email")
    val email: String? = null,
)