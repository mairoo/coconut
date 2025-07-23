package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.SocialApp

interface SocialAppRepository {
    fun save(
        socialApp: SocialApp,
    ): SocialApp

    fun findById(
        id: Int,
    ): SocialApp?
}