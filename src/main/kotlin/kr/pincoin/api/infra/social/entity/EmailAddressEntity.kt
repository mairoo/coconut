package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*

@Entity
@Table(name = "account_emailaddress")
class EmailAddressEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "email")
    val email: String,

    @Column(name = "verified")
    val verified: Boolean,

    @Column(name = "primary")
    val primary: Boolean,

    @Column(name = "user_id")
    val userId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            email: String,
            verified: Boolean = false,
            primary: Boolean = false,
            userId: Int
        ) = EmailAddressEntity(
            id = id,
            email = email,
            verified = verified,
            primary = primary,
            userId = userId
        )
    }
}