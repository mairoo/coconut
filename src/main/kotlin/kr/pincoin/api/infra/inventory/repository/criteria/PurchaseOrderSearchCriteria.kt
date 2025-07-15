package kr.pincoin.api.infra.inventory.repository.criteria

data class PurchaseOrderSearchCriteria(
    val purchaseOrderId: Long? = null,
    val title: String? = null,
    val content: String? = null,
    val bankAccount: String? = null,
    val paid: Boolean? = null,
    val isRemoved: Boolean? = null,
)