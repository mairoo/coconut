package kr.pincoin.api.external.auth.keycloak.api.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 사용자 정보 데이터
 */
data class KeycloakUserResponse(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("username")
    val username: String,

    @field:JsonProperty("enabled")
    val enabled: Boolean,

    @field:JsonProperty("emailVerified")
    val emailVerified: Boolean,

    @field:JsonProperty("firstName")
    val firstName: String? = null,

    @field:JsonProperty("lastName")
    val lastName: String? = null,

    @field:JsonProperty("email")
    val email: String? = null,

    @field:JsonProperty("createdTimestamp")
    val createdTimestamp: Long? = null,
)