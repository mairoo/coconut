package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.PurchaseOrder
import kr.pincoin.api.domain.inventory.repository.PurchaseOrderRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderRepositoryImpl(
    private val jpaRepository: PurchaseOrderJpaRepository,
    private val queryRepository: PurchaseOrderQueryRepository,
) : PurchaseOrderRepository {
    override fun save(
        purchaseOrder: PurchaseOrder,
    ): PurchaseOrder =
        purchaseOrder.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문발주 저장 실패")

    override fun findById(
        id: Long,
    ): PurchaseOrder? =
        queryRepository.findById(id)?.toModel()
}