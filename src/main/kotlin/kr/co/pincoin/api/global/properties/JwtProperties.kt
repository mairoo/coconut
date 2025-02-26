package kr.co.pincoin.api.global.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiresIn: Int,
    val refreshTokenExpiresIn: Long,
    val cookieDomain: String,
    val oauth2RedirectUrl: String
)