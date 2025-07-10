package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "socialaccount_socialaccount")
class SocialAccountEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "provider")
    val provider: String,

    @Column(name = "uid")
    val uid: String,

    @Column(name = "last_login")
    val lastLogin: LocalDateTime,

    @Column(name = "date_joined")
    val dateJoined: LocalDateTime,

    @Column(name = "extra_data")
    val extraData: String,

    @Column(name = "user_id")
    val userId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            uid: String,
            lastLogin: LocalDateTime,
            dateJoined: LocalDateTime,
            extraData: String,
            userId: Int
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