package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.SocialAppSites

interface SocialAppSitesRepository {
    fun save(
        socialAppSites: SocialAppSites,
    ): SocialAppSites

    fun findById(
        id: Int,
    ): SocialAppSites?
}