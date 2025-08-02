package kr.pincoin.api.app.oauth2.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * OAuth2 로그인 URL 응답
 */
data class OAuth2LoginUrlResponse(
    /**
     * Keycloak authorization URL
     */
    @JsonProperty("loginUrl")
    val loginUrl: String,

    /**
     * CSRF 공격 방어용 state 값
     */
    @JsonProperty("state")
    val state: String,

    /**
     * 세션 만료 시간 (초 단위)
     */
    @JsonProperty("expiresIn")
    val expiresIn: Long = 600L,
) {
    companion object {
        fun of(
            loginUrl: String,
            state: String,
            expiresIn: Long = 600L,
        ): OAuth2LoginUrlResponse =
            OAuth2LoginUrlResponse(
                loginUrl = loginUrl,
                state = state,
                expiresIn = expiresIn,
            )
    }
}