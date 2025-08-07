package kr.pincoin.api.external.auth.keycloak.api.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email

// 사용자 정보 수정
data class KeycloakUpdateUserRequest(
    @field:JsonProperty("firstName")
    val firstName: String? = null,

    @field:JsonProperty("lastName")
    val lastName: String? = null,

    @field:Email(message = "올바른 이메일 형식이어야 합니다")
    @field:JsonProperty("email")
    val email: String? = null,

    @field:JsonProperty("enabled")
    val enabled: Boolean? = null,

    @field:JsonProperty("emailVerified")
    val emailVerified: Boolean? = null,

    @field:JsonProperty("credentials")
    val credentials: List<KeycloakCreateUserRequest.KeycloakCredential>? = null,
)