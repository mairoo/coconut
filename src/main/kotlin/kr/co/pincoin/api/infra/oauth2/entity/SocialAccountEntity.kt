package kr.co.pincoin.api.infra.oauth2.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "socialaccount_socialaccount")
class SocialAccountEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "provider")
    val provider: String,

    @Column(name = "uid")
    val uid: String,

    @Column(name = "last_login")
    val lastLogin: ZonedDateTime,

    @Column(name = "date_joined")
    val dateJoined: ZonedDateTime,

    @Column(name = "extra_data", columnDefinition = "text")
    val extraData: String,

    @Column(name = "user_id")
    val userId: Int
) {
    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            uid: String,
            lastLogin: ZonedDateTime = ZonedDateTime.now(),
            dateJoined: ZonedDateTime = ZonedDateTime.now(),
            extraData: String,
            userId: Int,
        ) = SocialAccountEntity(
            id = id,
            provider = provider,
            uid = uid,
            lastLogin = lastLogin,
            dateJoined = dateJoined,
            extraData = extraData,
            userId = userId
        )
    }
}