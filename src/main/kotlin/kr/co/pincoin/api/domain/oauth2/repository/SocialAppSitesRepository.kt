package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialAppSites

interface SocialAppSitesRepository {
    fun save(
        socialAppSites: SocialAppSites,
    ): SocialAppSites
}