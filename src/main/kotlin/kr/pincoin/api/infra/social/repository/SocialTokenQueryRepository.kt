package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.SocialTokenEntity

interface SocialTokenQueryRepository {
    fun findById(
        id: Int,
    ): SocialTokenEntity?
}