package kr.co.pincoin.api.infra.user.entity

import jakarta.persistence.*
import java.net.InetAddress
import java.time.ZonedDateTime

@Entity
@Table(name = "member_loginlog")
class LoginLogEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "created")
    var created: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "modified")
    var modified: ZonedDateTime = ZonedDateTime.now(),

    @Column(name = "ip_address", columnDefinition = "inet")
    val ipAddress: InetAddress,

    @Column(name = "user_id")
    val userId: Int?,

    @Column(name = "email")
    val email: String?,

    @Column(name = "username")
    val username: String?,

    @Column(name = "user_agent")
    val userAgent: String?,

    @Column(name = "is_successful")
    val isSuccessful: Boolean,

    @Column(name = "reason")
    val reason: String?,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime = ZonedDateTime.now(),
            modified: ZonedDateTime = ZonedDateTime.now(),
            ipAddress: InetAddress,
            userId: Int?,
            email: String?,
            username: String?,
            userAgent: String?,
            isSuccessful: Boolean,
            reason: String?,
        ) = LoginLogEntity(
            id = id,
            created = created,
            modified = modified,
            ipAddress = ipAddress,
            userId = userId,
            email = email,
            username = username,
            userAgent = userAgent,
            isSuccessful = isSuccessful,
            reason = reason,
        )
    }
}