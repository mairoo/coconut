package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "socialaccount_socialtoken")
class SocialTokenEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "token")
    val token: String,

    @Column(name = "token_secret")
    val tokenSecret: String,

    @Column(name = "expires_at")
    val expiresAt: LocalDateTime?,

    @Column(name = "account_id")
    val accountId: Int,

    @Column(name = "app_id")
    val appId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            token: String,
            tokenSecret: String,
            expiresAt: LocalDateTime? = null,
            accountId: Int,
            appId: Int
        ) = SocialTokenEntity(
            id = id,
            token = token,
            tokenSecret = tokenSecret,
            expiresAt = expiresAt,
            accountId = accountId,
            appId = appId
        )
    }
}