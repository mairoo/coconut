package kr.pincoin.api.infra.social.entity

import jakarta.persistence.*

@Entity
@Table(name = "socialaccount_socialapp_sites")
class SocialAppSitesEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Int?,

    @Column(name = "socialapp_id")
    val socialAppId: Int,

    @Column(name = "site_id")
    val siteId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            socialAppId: Int,
            siteId: Int
        ) = SocialAppSitesEntity(
            id = id,
            socialAppId = socialAppId,
            siteId = siteId
        )
    }
}