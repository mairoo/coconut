package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.PurchaseOrderPaymentEntity

interface PurchaseOrderPaymentQueryRepository {
    fun findById(
        id: Long,
    ): PurchaseOrderPaymentEntity?
}