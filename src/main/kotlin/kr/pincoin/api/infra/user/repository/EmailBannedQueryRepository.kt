package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.EmailBannedEntity

interface EmailBannedQueryRepository {
    fun findById(
        id: Long,
    ): EmailBannedEntity?
}