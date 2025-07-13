package kr.pincoin.api.domain.social.repository

import kr.pincoin.api.domain.social.model.SocialAccount

interface SocialAccountRepository {
    fun save(
        socialAccount: SocialAccount,
    ): SocialAccount

    fun findById(
        id: Int,
    ): SocialAccount?
}