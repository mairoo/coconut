package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.PurchaseOrderEntity
import kr.pincoin.api.infra.inventory.entity.QPurchaseOrderEntity
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : PurchaseOrderQueryRepository {
    private val purchaseOrder = QPurchaseOrderEntity.purchaseOrderEntity

    override fun findById(
        id: Long,
    ): PurchaseOrderEntity? =
        queryFactory
            .selectFrom(purchaseOrder)
            .where(purchaseOrder.id.eq(id))
            .fetchOne()
}