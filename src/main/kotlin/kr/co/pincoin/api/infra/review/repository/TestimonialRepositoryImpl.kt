package kr.co.pincoin.api.infra.review.repository

import kr.co.pincoin.api.domain.review.model.Testimonial
import kr.co.pincoin.api.domain.review.repository.TestimonialRepository
import kr.co.pincoin.api.infra.review.mapper.toEntity
import kr.co.pincoin.api.infra.review.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class TestimonialRepositoryImpl(
    private val jpaRepository: TestimonialJpaRepository,
) : TestimonialRepository {
    override fun save(
        testimonial: Testimonial,
    ): Testimonial =
        testimonial.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("이용후기 저장 실패")
}