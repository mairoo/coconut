package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.QTestimonialAnswerEntity
import kr.pincoin.api.infra.support.entity.TestimonialAnswerEntity
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TestimonialAnswerQueryRepository {
    private val testimonialAnswer = QTestimonialAnswerEntity.testimonialAnswerEntity

    override fun findById(
        id: Long,
    ): TestimonialAnswerEntity? =
        queryFactory
            .selectFrom(testimonialAnswer)
            .where(testimonialAnswer.id.eq(id))
            .fetchOne()
}