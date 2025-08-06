package kr.pincoin.api.app.oauth2.request

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank

data class OAuth2CallbackRequest(
    @field:NotBlank(message = "Authorization code는 필수입니다")
    @JsonProperty("code")
    val code: String,

    @field:NotBlank(message = "State 파라미터는 필수입니다")
    @JsonProperty("state")
    val state: String,

    @field:NotBlank(message = "Redirect URI는 필수입니다")
    @JsonProperty("redirectUri")
    val redirectUri: String,

    // 에러 파라미터 (선택적, Keycloak에서 에러 시 전달)
    val error: String? = null,
    val errorDescription: String? = null,
)