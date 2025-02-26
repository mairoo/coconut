package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.oauth2.model.SocialToken

interface SocialTokenRepository {
    fun save(
        socialToken: SocialToken,
    ): SocialToken
}