package kr.co.pincoin.api.infra.message.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class FaqMessageQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FaqMessageQueryRepository {
}