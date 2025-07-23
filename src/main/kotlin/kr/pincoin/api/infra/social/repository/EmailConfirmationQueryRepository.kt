package kr.pincoin.api.infra.social.repository

import kr.pincoin.api.infra.social.entity.EmailConfirmationEntity

interface EmailConfirmationQueryRepository {
    fun findById(
        id: Int,
    ): EmailConfirmationEntity?
}