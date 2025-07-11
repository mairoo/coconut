package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields

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
    val ipAddress: String,

    @Column(name = "user_id")
    val userId: Int?,
) {
    companion object {
        fun of(
            id: Long? = null,
            ipAddress: String,
            userId: Int? = null
        ) = LoginLogEntity(
            id = id,
            ipAddress = ipAddress,
            userId = userId
        )
    }
}