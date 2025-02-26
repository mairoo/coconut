package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestionAnswer
import kr.co.pincoin.api.domain.inquiry.repository.CustomerQuestionAnswerRepository
import kr.co.pincoin.api.infra.inquiry.mapper.toEntity
import kr.co.pincoin.api.infra.inquiry.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionAnswerRepositoryImpl(
    private val jpaRepository: CustomerQuestionAnswerJpaRepository,
    private val queryRepository: CustomerQuestionAnswerQueryRepository,
) : CustomerQuestionAnswerRepository {
    override fun save(
        customerQuestionAnswer: CustomerQuestionAnswer,
    ): CustomerQuestionAnswer =
        customerQuestionAnswer.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("고객문의답변 저장 실패")
}