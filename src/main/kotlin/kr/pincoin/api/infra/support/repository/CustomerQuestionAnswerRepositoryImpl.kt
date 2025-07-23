package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.CustomerQuestionAnswer
import kr.pincoin.api.domain.support.repository.CustomerQuestionAnswerRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
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

    override fun findById(id: Long): CustomerQuestionAnswer? =
        queryRepository.findById(id)?.toModel()
}