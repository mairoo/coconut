package kr.co.pincoin.api.infra.inquiry.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CustomerQuestionQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CustomerQuestionQueryRepository {
}