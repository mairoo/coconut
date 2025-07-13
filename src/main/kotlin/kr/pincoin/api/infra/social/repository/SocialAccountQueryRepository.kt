package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.SocialAccountEntity

interface SocialAccountQueryRepository {
    fun findById(
        id: Int,
    ): SocialAccountEntity?
}