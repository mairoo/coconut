package kr.co.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PurchaseOrderPaymentQueryRepository {
}