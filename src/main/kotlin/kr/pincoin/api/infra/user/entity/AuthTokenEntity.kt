package kr.pincoin.api.infra.user.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "authtoken_token")
class AuthTokenEntity private constructor(
    @Id
    @Column(name = "key")
    val key: String,

    @Column(name = "user_id")
    val userId: Int,

    @Column(name = "created")
    val created: LocalDateTime,
) {
    companion object {
        fun of(
            key: String,
            userId: Int,
            created: LocalDateTime = LocalDateTime.now(),
        ) = AuthTokenEntity(
            key = key,
            userId = userId,
            created = created,
        )
    }
}