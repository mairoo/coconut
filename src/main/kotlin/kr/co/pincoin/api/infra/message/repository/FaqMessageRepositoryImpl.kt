package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.domain.message.model.FaqMessage
import kr.co.pincoin.api.domain.message.repository.FaqMessageRepository
import kr.co.pincoin.api.infra.message.mapper.toEntity
import kr.co.pincoin.api.infra.message.mapper.toModel
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
            ?: throw IllegalArgumentException("자주묻는질문 저장 실패")
}