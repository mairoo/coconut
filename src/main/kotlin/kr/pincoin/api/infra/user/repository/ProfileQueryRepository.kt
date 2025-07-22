package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.ProfileEntity

interface ProfileQueryRepository {
    fun findById(
        id: Long,
    ): ProfileEntity?
}