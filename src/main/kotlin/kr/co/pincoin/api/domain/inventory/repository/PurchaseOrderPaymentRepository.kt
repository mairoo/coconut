package kr.co.pincoin.api.domain.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrderPayment

interface PurchaseOrderPaymentRepository {
    fun save(
        purchaseOrderPayment: PurchaseOrderPayment,
    ): PurchaseOrderPayment
}