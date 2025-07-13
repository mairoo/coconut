package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.SocialAppSitesEntity

interface SocialAppSitesQueryRepository {
    fun findById(
        id: Int,
    ): SocialAppSitesEntity?
}