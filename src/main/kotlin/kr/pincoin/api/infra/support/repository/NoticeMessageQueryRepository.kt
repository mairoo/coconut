package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.NoticeMessageEntity

interface NoticeMessageQueryRepository {
    fun findById(
        id: Long,
    ): NoticeMessageEntity?
}