package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.Testimonial
import kr.pincoin.api.domain.support.repository.TestimonialRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class TestimonialRepositoryImpl(
    private val jpaRepository: TestimonialJpaRepository,
    private val queryRepository: TestimonialQueryRepository,
) : TestimonialRepository {
    override fun save(
        testimonial: Testimonial,
    ): Testimonial =
        testimonial.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("고객리뷰 저장 실패")

    override fun findById(id: Long): Testimonial? =
        queryRepository.findById(id)?.toModel()
}