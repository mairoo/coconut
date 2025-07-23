package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.FaqMessageEntity

interface FaqMessageQueryRepository {
    fun findById(
        id: Long,
    ): FaqMessageEntity?
}