package kr.pincoin.api.infra.inventory.repository.criteria

data class VoucherSearchCriteria(
    val voucherId: Long? = null,
    val code: String? = null,
    val remarks: String? = null,
    val status: Int? = null,
    val productId: Long? = null,
    val isRemoved: Boolean? = null,
)