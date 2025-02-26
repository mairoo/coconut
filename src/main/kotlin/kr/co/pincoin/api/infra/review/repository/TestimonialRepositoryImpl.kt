package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.model.Testimonial
import kr.co.pincoin.api.domain.review.repository.TestimonialRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialRepositoryImpl(
    private val jpaRepository: TestimonialJpaRepository,
) : TestimonialRepository {
    override fun save(testimonial: Testimonial): Testimonial {
        TODO("Not yet implemented")
    }
}