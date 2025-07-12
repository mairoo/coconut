package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.CustomerQuestionRepository
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionRepositoryImpl(
    private val jpaRepository: CustomerQuestionJpaRepository,
    private val queryRepository: CustomerQuestionQueryRepository,
) : CustomerQuestionRepository {
}