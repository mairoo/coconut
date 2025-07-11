package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes

@Entity
@Table(name = "member_loginlog")
class LoginLogEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "ip_address", columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    val ipAddress: String,

    @Column(name = "user_id")
    val userId: Int?,

    @Column(name = "email")
    val email: String?,

    @Column(name = "user_agent")
    val userAgent: String?,

    @Column(name = "is_successful")
    val isSuccessful: Boolean?,

    @Column(name = "reason")
    val reason: String?,
) {
    companion object {
        fun of(
            id: Long? = null,
            ipAddress: String,
            userId: Int? = null,
            email: String?,
            userAgent: String?,
            isSuccessful: Boolean? = false,
            reason: String? = null,
        ) = LoginLogEntity(
            id = id,
            ipAddress = ipAddress,
            userId = userId,
            email = email,
            userAgent = userAgent,
            isSuccessful = isSuccessful,
            reason = reason,
        )
    }
}