package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.CustomerQuestionAnswerRepository
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionAnswerRepositoryImpl(
    private val jpaRepository: CustomerQuestionAnswerJpaRepository,
    private val queryRepository: CustomerQuestionAnswerQueryRepository,
) : CustomerQuestionAnswerRepository {
}