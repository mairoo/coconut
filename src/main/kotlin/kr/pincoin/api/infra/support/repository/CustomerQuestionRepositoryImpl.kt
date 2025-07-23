package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.CustomerQuestion
import kr.pincoin.api.domain.support.repository.CustomerQuestionRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
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

    override fun findById(
        id: Long,
    ): CustomerQuestion? =
        queryRepository.findById(id)?.toModel()
}