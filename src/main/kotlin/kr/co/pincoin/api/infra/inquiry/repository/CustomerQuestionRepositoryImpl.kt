package kr.co.pincoin.api.infra.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.repository.CustomerQuestionRepository
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionRepositoryImpl(
    private val jpaRepository: CustomerQuestionJpaRepository,
    private val queryRepository: CustomerQuestionQueryRepository,
) : CustomerQuestionRepository {
}