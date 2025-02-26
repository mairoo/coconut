package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.repository.TestimonialAnswerRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerRepositoryImpl(
    private val jpaRepository: TestimonialAnswerJpaRepository,
) : TestimonialAnswerRepository {
}