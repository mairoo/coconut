package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestion
import kr.co.pincoin.api.domain.inquiry.repository.CustomerQuestionRepository
import kr.co.pincoin.api.infra.inquiry.mapper.toEntity
import kr.co.pincoin.api.infra.inquiry.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionRepositoryImpl(
    private val jpaRepository: CustomerQuestionJpaRepository,
    private val queryRepository: CustomerQuestionQueryRepository,
) : CustomerQuestionRepository {
    override fun save(
        customerQuestion: CustomerQuestion,
    ): CustomerQuestion =
        customerQuestion.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("고객문의 저장 실패")
}