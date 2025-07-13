package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.PurchaseOrderPayment

interface PurchaseOrderPaymentRepository {
    fun save(
        purchaseOrderPayment: PurchaseOrderPayment,
    ): PurchaseOrderPayment

    fun findById(
        id: Long,
    ): PurchaseOrderPayment?
}