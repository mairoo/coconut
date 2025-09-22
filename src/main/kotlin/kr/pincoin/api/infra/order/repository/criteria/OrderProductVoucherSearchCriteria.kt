package kr.pincoin.api.infra.order.repository.criteria

import java.time.LocalDateTime

data class OrderProductVoucherSearchCriteria(
    // OrderProductVoucher 필드
    val voucherId: Long? = null,
    val orderProductId: Long? = null,
    val code: String? = null,
    val remarks: String? = null,
    val revoked: Boolean? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val isActive: Boolean? = null,
    val isRemoved: Boolean? = null,

    // OrderProduct 필드 (연관된 주문상품 정보)
    val orderId: Long? = null,
    val quantity: Int? = null,

    // Order 필드 (연관된 주문 정보)
    val orderNumber: String? = null,
    val orderStatus: String? = null,
    val orderIsActive: Boolean? = null,
    val orderIsRemoved: Boolean? = null,

    // User 필드 (주문자 정보)
    val userId: Int? = null,
    val userEmail: String? = null,
    val userIsActive: Boolean? = null,
)