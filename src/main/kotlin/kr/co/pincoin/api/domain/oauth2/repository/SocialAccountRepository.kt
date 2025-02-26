package kr.co.pincoin.api.domain.oauth2.repository

import kr.co.pincoin.api.domain.oauth2.model.SocialAccount

interface SocialAccountRepository {
    fun save(
        socialAccount: SocialAccount,
    ): SocialAccount
}