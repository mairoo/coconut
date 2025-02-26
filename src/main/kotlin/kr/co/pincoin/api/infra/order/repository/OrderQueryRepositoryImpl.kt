package kr.co.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderQueryRepository {
}