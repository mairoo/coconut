package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrder
import kr.co.pincoin.api.domain.inventory.repository.PurchaseOrderRepository
import kr.co.pincoin.api.infra.inventory.mapper.toEntity
import kr.co.pincoin.api.infra.inventory.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderRepositoryImpl(
    private val jpaRepository: PurchaseOrderJpaRepository,
    private val queryRepository: PurchaseOrderQueryRepository,
) : PurchaseOrderRepository {
    override fun save(purchaseOrder: PurchaseOrder): PurchaseOrder =
        purchaseOrder.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("매입주문 저장 실패")
}