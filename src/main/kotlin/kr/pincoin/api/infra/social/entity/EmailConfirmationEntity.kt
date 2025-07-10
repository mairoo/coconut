package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "account_emailconfirmation")
class EmailConfirmationEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "created")
    val created: LocalDateTime,

    @Column(name = "sent")
    val sent: LocalDateTime?,

    @Column(name = "key")
    val key: String,

    @Column(name = "email_address_id")
    val emailAddressId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            created: LocalDateTime = LocalDateTime.now(),
            sent: LocalDateTime? = null,
            key: String,
            emailAddressId: Int
        ) = EmailConfirmationEntity(
            id = id,
            created = created,
            sent = sent,
            key = key,
            emailAddressId = emailAddressId
        )
    }
}