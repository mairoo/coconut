package kr.pincoin.api.infra.user.repository

import kr.pincoin.api.domain.user.model.PhoneBanned
import kr.pincoin.api.domain.user.repository.PhoneBannedRepository
import kr.pincoin.api.infra.user.mapper.toEntity
import kr.pincoin.api.infra.user.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class PhoneBannedRepositoryImpl(
    private val jpaRepository: PhoneBannedJpaRepository,
    private val queryRepository: PhoneBannedQueryRepository
) : PhoneBannedRepository {
    override fun save(
        phoneBanned: PhoneBanned,
    ): PhoneBanned =
        phoneBanned.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("차단 휴대전화번호 저장 실패")

    override fun findById(
        id: Long,
    ): PhoneBanned? =
        queryRepository.findById(id)?.toModel()
}