package kr.co.pincoin.api.infra.review.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class TestimonialAnswerQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TestimonialQueryRepository {
}