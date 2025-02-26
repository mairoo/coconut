package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialApp

interface SocialAppRepository {
    fun save(
        socialApp: SocialApp,
    ): SocialApp
}