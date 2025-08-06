package kr.pincoin.api.app.oauth2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuth2LoginUrlRequest(
    @field:JsonProperty("redirectUri")
    val redirectUri: String,
)