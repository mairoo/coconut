package kr.co.pincoin.api.domain.oauth2.model

import java.time.ZonedDateTime

class SocialToken private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val accountId: Int,
    val appId: Int,

    // 3. 도메인 로직 가변 필드
    val token: String,
    val tokenSecret: String,
    val expiresAt: ZonedDateTime?,
) {
    fun updateToken(
        newToken: String? = null,
        newTokenSecret: String? = null,
        newExpiresAt: ZonedDateTime? = null
    ): SocialToken =
        copy(
            token = newToken ?: token,
            tokenSecret = newTokenSecret ?: tokenSecret,
            expiresAt = newExpiresAt ?: expiresAt
        )

    private fun copy(
        token: String? = null,
        tokenSecret: String? = null,
        expiresAt: ZonedDateTime? = null
    ): SocialToken = SocialToken(
        id = this.id,
        accountId = this.accountId,
        appId = this.appId,
        token = token ?: this.token,
        tokenSecret = tokenSecret ?: this.tokenSecret,
        expiresAt = expiresAt ?: this.expiresAt
    )

    companion object {
        fun of(
            id: Int? = null,
            token: String,
            tokenSecret: String,
            expiresAt: ZonedDateTime? = null,
            accountId: Int,
            appId: Int,
        ): SocialToken =
            SocialToken(
                id = id,
                token = token,
                tokenSecret = tokenSecret,
                expiresAt = expiresAt,
                accountId = accountId,
                appId = appId,
            )
    }
}