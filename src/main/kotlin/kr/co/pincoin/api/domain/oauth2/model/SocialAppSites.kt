package kr.co.pincoin.api.domain.oauth2.model

class SocialAppSites private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,

    // 2. 도메인 로직 불변 필드
    val socialAppId: Int,
    val siteId: Int,
) {
    companion object {
        fun of(
            id: Int? = null,
            socialAppId: Int,
            siteId: Int,
        ): SocialAppSites =
            SocialAppSites(
                id = id,
                socialAppId = socialAppId,
                siteId = siteId,
            )
    }
}