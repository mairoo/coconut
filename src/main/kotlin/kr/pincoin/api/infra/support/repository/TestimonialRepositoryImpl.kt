package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.repository.TestimonialRepository
import org.springframework.stereotype.Repository

@Repository
class TestimonialRepositoryImpl(
    private val jpaRepository: TestimonialJpaRepository,
    private val queryRepository: TestimonialQueryRepository,
) : TestimonialRepository {
}