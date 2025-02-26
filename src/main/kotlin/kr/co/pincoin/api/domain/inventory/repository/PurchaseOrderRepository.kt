package kr.co.pincoin.api.domain.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrder

interface PurchaseOrderRepository {
    fun save(
        purchaseOrder: PurchaseOrder,
    ): PurchaseOrder
}