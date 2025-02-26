package kr.co.pincoin.api.infra.oauth2.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "socialaccount_socialtoken")
class SocialTokenEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "token", columnDefinition = "text")
    val token: String,

    @Column(name = "token_secret", columnDefinition = "text")
    val tokenSecret: String,

    @Column(name = "expires_at")
    val expiresAt: ZonedDateTime? = null,

    @Column(name = "account_id")
    val accountId: Int,

    @Column(name = "app_id")
    val appId: Int
) {
    companion object {
        fun of(
            id: Int? = null,
            token: String,
            tokenSecret: String,
            expiresAt: ZonedDateTime? = null,
            accountId: Int,
            appId: Int,
        ) = SocialTokenEntity(
            id = id,
            token = token,
            tokenSecret = tokenSecret,
            expiresAt = expiresAt,
            accountId = accountId,
            appId = appId,
        )
    }
}