package kr.co.pincoin.api.infra.review.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class TestimonialQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TestimonialQueryRepository {
}