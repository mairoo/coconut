package kr.co.pincoin.api.infra.oauth2.entity

import jakarta.persistence.*
import java.time.ZonedDateTime

@Entity
@Table(name = "account_emailconfirmation")
class EmailConfirmationEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int? = null,

    @Column(name = "created")
    val created: ZonedDateTime,

    @Column(name = "sent")
    val sent: ZonedDateTime? = null,

    @Column(name = "key", unique = true)
    val key: String,

    @Column(name = "email_address_id")
    val emailAddressId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            created: ZonedDateTime = ZonedDateTime.now(),
            sent: ZonedDateTime? = null,
            key: String,
            emailAddressId: Int,
        ) = EmailConfirmationEntity(
            id = id,
            created = created,
            sent = sent,
            key = key,
            emailAddressId = emailAddressId
        )
    }
}