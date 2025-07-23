package kr.pincoin.api.domain.social.model

import java.time.LocalDateTime

class SocialToken private constructor(
    val id: Int? = null,
    val token: String,
    val tokenSecret: String,
    val expiresAt: LocalDateTime? = null,
    val accountId: Int,
    val appId: Int
) {
    fun updateToken(newToken: String): SocialToken =
        copy(token = newToken)

    fun updateTokenSecret(newTokenSecret: String): SocialToken =
        copy(tokenSecret = newTokenSecret)

    fun updateExpiresAt(newExpiresAt: LocalDateTime?): SocialToken =
        copy(expiresAt = newExpiresAt)

    fun isExpired(): Boolean =
        expiresAt?.isBefore(LocalDateTime.now()) ?: false

    fun isValid(): Boolean =
        token.isNotBlank() && !isExpired()

    fun willExpireSoon(minutes: Long = 30): Boolean =
        expiresAt?.isBefore(LocalDateTime.now().plusMinutes(minutes)) ?: false

    private fun copy(
        token: String = this.token,
        tokenSecret: String = this.tokenSecret,
        expiresAt: LocalDateTime? = this.expiresAt,
        accountId: Int = this.accountId,
        appId: Int = this.appId
    ): SocialToken = SocialToken(
        id = this.id,
        token = token,
        tokenSecret = tokenSecret,
        expiresAt = expiresAt,
        accountId = accountId,
        appId = appId
    )

    companion object {
        fun of(
            id: Int? = null,
            token: String,
            tokenSecret: String,
            expiresAt: LocalDateTime? = null,
            accountId: Int,
            appId: Int
        ): SocialToken {
            require(token.isNotBlank()) { "토큰은 필수 입력값입니다" }
            require(accountId > 0) { "계정 ID는 양수여야 합니다" }
            require(appId > 0) { "앱 ID는 양수여야 합니다" }

            return SocialToken(
                id = id,
                token = token,
                tokenSecret = tokenSecret,
                expiresAt = expiresAt,
                accountId = accountId,
                appId = appId
            )
        }
    }
}