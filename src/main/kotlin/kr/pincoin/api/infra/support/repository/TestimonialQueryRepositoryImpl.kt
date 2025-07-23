package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.QTestimonialEntity
import kr.pincoin.api.infra.support.entity.TestimonialEntity
import org.springframework.stereotype.Repository

@Repository
class TestimonialQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TestimonialQueryRepository {
    private val testimonial = QTestimonialEntity.testimonialEntity

    override fun findById(
        id: Long,
    ): TestimonialEntity? =
        queryFactory
            .selectFrom(testimonial)
            .where(testimonial.id.eq(id))
            .fetchOne()
}