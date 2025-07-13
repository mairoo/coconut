package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.PhoneVerificationLog
import kr.pincoin.api.domain.user.repository.PhoneVerificationLogRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class PhoneVerificationLogRepositoryImpl(
    private val jpaRepository: PhoneVerificationLogJpaRepository,
    private val queryRepository: PhoneVerificationLogQueryRepository,
) : PhoneVerificationLogRepository {
    override fun save(
        phoneVerificationLog: PhoneVerificationLog,
    ): PhoneVerificationLog =
        phoneVerificationLog.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("휴대폰인증로그 저장 실패")

    override fun findById(
        id: Long,
    ): PhoneVerificationLog? =
        queryRepository.findById(id)?.toModel()
}