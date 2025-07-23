package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*

@Entity
@Table(name = "socialaccount_socialapp")
class SocialAppEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "provider")
    val provider: String,

    @Column(name = "name")
    val name: String,

    @Column(name = "client_id")
    val clientId: String,

    @Column(name = "secret")
    val secret: String,

    @Column(name = "key")
    val key: String,
) {
    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            name: String,
            clientId: String,
            secret: String,
            key: String
        ) = SocialAppEntity(
            id = id,
            provider = provider,
            name = name,
            clientId = clientId,
            secret = secret,
            key = key
        )
    }
}