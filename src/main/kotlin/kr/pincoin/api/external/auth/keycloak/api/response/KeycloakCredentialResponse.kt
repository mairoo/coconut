package kr.pincoin.api.external.auth.keycloak.api.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 사용자 인증정보 조회 응답
 */
data class KeycloakCredentialResponse(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("type")
    val type: String,

    @field:JsonProperty("userLabel")
    val userLabel: String? = null,

    @field:JsonProperty("createdDate")
    val createdDate: Long? = null,

    @field:JsonProperty("secretData")
    val secretData: String? = null,

    @field:JsonProperty("credentialData")
    val credentialData: String? = null,
)