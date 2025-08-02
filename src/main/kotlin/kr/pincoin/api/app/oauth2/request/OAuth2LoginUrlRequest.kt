package kr.pincoin.api.app.oauth2.request

import com.fasterxml.jackson.annotation.JsonProperty

data class OAuth2LoginUrlRequest(
    @JsonProperty("redirect_uri")
    val redirectUri: String,
)