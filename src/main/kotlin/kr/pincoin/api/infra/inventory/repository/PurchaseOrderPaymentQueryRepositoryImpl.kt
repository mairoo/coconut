package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.PurchaseOrderPaymentEntity
import kr.pincoin.api.infra.inventory.entity.QPurchaseOrderPaymentEntity
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PurchaseOrderPaymentQueryRepository {
    private val purchaseOrderPayment = QPurchaseOrderPaymentEntity.purchaseOrderPaymentEntity

    override fun findById(
        id: Long,
    ): PurchaseOrderPaymentEntity? =
        queryFactory
            .selectFrom(purchaseOrderPayment)
            .where(purchaseOrderPayment.id.eq(id))
            .fetchOne()
}