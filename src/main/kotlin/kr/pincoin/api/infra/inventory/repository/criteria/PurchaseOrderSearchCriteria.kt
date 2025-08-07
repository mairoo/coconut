package kr.pincoin.api.infra.inventory.repository.criteria

import java.math.BigDecimal

data class PurchaseOrderSearchCriteria(
    val purchaseOrderId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val bankAccount: String? = null,
    val amount: BigDecimal? = null,
    val paid: Boolean? = null,
    val isRemoved: Boolean? = null,
)