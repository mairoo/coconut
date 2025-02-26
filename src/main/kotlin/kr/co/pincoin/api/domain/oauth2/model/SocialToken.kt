package kr.co.pincoin.api.domain.oauth2.model

import java.time.ZonedDateTime

class SocialToken private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val accountId: Int,
    val appId: Int,

    // 3. 도메인 로직 가변 필드
    token: String,
    tokenSecret: String,
    expiresAt: ZonedDateTime?,
) {
    var token: String = token
        private set

    var tokenSecret: String = tokenSecret
        private set

    var expiresAt: ZonedDateTime? = expiresAt
        private set

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