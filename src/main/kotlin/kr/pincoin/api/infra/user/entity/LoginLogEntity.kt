package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "member_loginlog")
class LoginLogEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "modified")
    val modified: LocalDateTime,

    @Column(name = "ip_address", columnDefinition = "inet")
    val ipAddress: String,

    @Column(name = "user_id")
    val userId: Int?,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
            ipAddress: String,
            userId: Int? = null
        ) = LoginLogEntity(
            id = id,
            created = created,
            modified = modified,
            ipAddress = ipAddress,
            userId = userId
        )
    }
}