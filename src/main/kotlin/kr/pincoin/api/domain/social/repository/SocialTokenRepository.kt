package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.SocialToken

interface SocialTokenRepository {
    fun save(
        socialToken: SocialToken,
    ): SocialToken

    fun findById(
        id: Int,
    ): SocialToken?
}