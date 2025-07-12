package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderPaymentQueryRepository {
}