package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.PurchaseOrder

interface PurchaseOrderRepository {
    fun save(
        purchaseOrder: PurchaseOrder,
    ): PurchaseOrder

    fun findById(
        id: Long,
    ): PurchaseOrder?
}