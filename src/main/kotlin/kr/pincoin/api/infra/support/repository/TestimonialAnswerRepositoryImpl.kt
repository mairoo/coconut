package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.TestimonialAnswerRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerRepositoryImpl(
    private val jpaRepository: TestimonialAnswerJpaRepository,
    private val queryRepository: TestimonialAnswerQueryRepository,
) : TestimonialAnswerRepository {
}