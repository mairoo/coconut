package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.PurchaseOrderEntity

interface PurchaseOrderQueryRepository {
    fun findById(
        id: Long,
    ): PurchaseOrderEntity?
}