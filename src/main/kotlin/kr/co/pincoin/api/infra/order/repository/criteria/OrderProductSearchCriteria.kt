package kr.co.pincoin.api.infra.order.repository.criteria

data class OrderProductSearchCriteria(
    val id: Long? = null,
    val orderId: Long? = null,
    val code: String? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val isRemoved: Boolean? = null,
)