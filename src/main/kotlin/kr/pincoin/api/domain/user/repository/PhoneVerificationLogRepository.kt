package kr.pincoin.api.domain.user.repository

import kr.pincoin.api.domain.user.model.PhoneVerificationLog

interface PhoneVerificationLogRepository {
    fun save(
        phoneVerificationLog: PhoneVerificationLog,
    ): PhoneVerificationLog

    fun findById(
        id: Long,
    ): PhoneVerificationLog?
}