package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.FaqMessage
import kr.pincoin.api.domain.support.repository.FaqMessageRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class FaqMessageRepositoryImpl(
    private val jpaRepository: FaqMessageJpaRepository,
    private val queryRepository: FaqMessageQueryRepository,
) : FaqMessageRepository {
    override fun save(
        faqMessage: FaqMessage,
    ): FaqMessage =
        faqMessage.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("FAQ 저장 실패")

    override fun findById(
        id: Long,
    ): FaqMessage? =
        queryRepository.findById(id)?.toModel()
}