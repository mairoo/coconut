package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.model.TestimonialAnswer
import kr.co.pincoin.api.domain.review.repository.TestimonialAnswerRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerRepositoryImpl(
    private val jpaRepository: TestimonialAnswerJpaRepository,
) : TestimonialAnswerRepository {
    override fun save(testimonialAnswer: TestimonialAnswer): TestimonialAnswer {
        TODO("Not yet implemented")
    }
}