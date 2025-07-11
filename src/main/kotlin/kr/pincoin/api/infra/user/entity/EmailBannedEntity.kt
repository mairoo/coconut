package kr.pincoin.api.infra.user.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields

@Entity
@Table(name = "member_emailbanned")
class EmailBannedEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "email")
    val email: String,
) {
    companion object {
        fun of(
            id: Long? = null,
            email: String,
            isRemoved: Boolean = false,
        ) = EmailBannedEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            email = email,
        )
    }
}