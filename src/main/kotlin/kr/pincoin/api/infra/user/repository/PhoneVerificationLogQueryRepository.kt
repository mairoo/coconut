package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.infra.user.entity.PhoneVerificationLogEntity

interface PhoneVerificationLogQueryRepository {
    fun findById(
        id: Long,
    ): PhoneVerificationLogEntity?
}