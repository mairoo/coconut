package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.repository.CustomerQuestionAnswerRepository
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionAnswerRepositoryImpl(
    private val jpaRepository: CustomerQuestionAnswerJpaRepository,
) : CustomerQuestionAnswerRepository {
}