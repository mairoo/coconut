package kr.co.pincoin.api.infra.order.repository.criteria

data class OrderProductVoucherSearchCriteria(
    val id: Long? = null,
    val orderProductId: Long? = null,
    val voucherId: Long? = null,
    val code: String? = null,
    val remarks: String? = null,
    val revoked: Boolean? = null,
    val isRemoved: Boolean? = null,

    val productCode: String? = null,
    val productName: String? = null,
)