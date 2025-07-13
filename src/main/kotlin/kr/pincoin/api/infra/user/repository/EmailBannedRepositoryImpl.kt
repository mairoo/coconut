package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.EmailBanned
import kr.pincoin.api.domain.user.repository.EmailBannedRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class EmailBannedRepositoryImpl(
    private val jpaRepository: EmailBannedJpaRepository,
    private val queryRepository: EmailBannedQueryRepository,
) : EmailBannedRepository {
    override fun save(
        emailBanned: EmailBanned,
    ): EmailBanned =
        emailBanned.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("차단 이메일 저장 실패")

    override fun findById(
        id: Long,
    ): EmailBanned? =
        queryRepository.findById(id)?.toModel()
}