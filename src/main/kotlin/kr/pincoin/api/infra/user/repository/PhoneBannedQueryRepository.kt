package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.PhoneBannedEntity

interface PhoneBannedQueryRepository {
    fun findById(
        id: Long,
    ): PhoneBannedEntity?
}