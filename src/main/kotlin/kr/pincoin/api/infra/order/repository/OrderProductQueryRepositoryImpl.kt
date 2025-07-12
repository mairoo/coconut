package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrderProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductQueryRepository {
}