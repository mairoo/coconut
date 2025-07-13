package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.SocialAppEntity

interface SocialAppQueryRepository {
    fun findById(
        id: Int,
    ): SocialAppEntity?
}