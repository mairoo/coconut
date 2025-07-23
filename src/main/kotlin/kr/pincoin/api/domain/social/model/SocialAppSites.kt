package kr.pincoin.api.domain.social.model

class SocialAppSites private constructor(
    val id: Int? = null,
    val socialAppId: Int,
    val siteId: Int
) {
    fun isSameSocialApp(targetSocialAppId: Int): Boolean =
        socialAppId == targetSocialAppId

    fun isSameSite(targetSiteId: Int): Boolean =
        siteId == targetSiteId

    fun isValidAssociation(): Boolean =
        socialAppId > 0 && siteId > 0

    companion object {
        fun of(
            id: Int? = null,
            socialAppId: Int,
            siteId: Int
        ): SocialAppSites {
            require(socialAppId > 0) { "소셜 앱 ID는 양수여야 합니다" }
            require(siteId > 0) { "사이트 ID는 양수여야 합니다" }

            return SocialAppSites(
                id = id,
                socialAppId = socialAppId,
                siteId = siteId
            )
        }
    }
}