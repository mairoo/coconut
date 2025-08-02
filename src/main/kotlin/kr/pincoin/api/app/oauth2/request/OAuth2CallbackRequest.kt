package kr.pincoin.api.app.oauth2.request

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OAuth2 콜백 요청
 *
 * Keycloak에서 Authorization Code를 받은 후
 * 프론트엔드가 백엔드로 전달하는 요청
 */
data class OAuth2CallbackRequest(
    /**
     * Keycloak에서 발급받은 Authorization Code
     */
    @JsonProperty("code")
    val code: String,

    /**
     * CSRF 방어용 state 값 (로그인 URL 생성 시 발급받은 값)
     */
    @JsonProperty("state")
    val state: String,

    /**
     * 원래 요청했던 redirect URI (검증용)
     */
    @JsonProperty("redirectUri")
    val redirectUri: String,
)