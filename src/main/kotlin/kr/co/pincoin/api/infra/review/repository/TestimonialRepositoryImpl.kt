package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.repository.TestimonialRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialRepositoryImpl(
    private val jpaRepository: TestimonialJpaRepository,
) : TestimonialRepository {
}