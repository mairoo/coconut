package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class TestimonialQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : TestimonialQueryRepository {
}