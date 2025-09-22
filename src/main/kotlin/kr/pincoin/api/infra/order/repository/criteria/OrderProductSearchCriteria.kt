package kr.pincoin.api.infra.order.repository.criteria

import java.time.LocalDateTime

data class OrderProductSearchCriteria(
    // OrderProduct 필드
    val orderId: Long? = null,
    val quantity: Int? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val isActive: Boolean? = null,
    val isRemoved: Boolean? = null,

    // Order 필드 (연관된 주문 정보)
    val orderNumber: String? = null,
    val orderStatus: String? = null,
    val orderIsActive: Boolean? = null,
    val orderIsRemoved: Boolean? = null,

    // Product 필드 (연관된 상품 정보)
    val productName: String? = null,
    val productCode: String? = null,
    val productStatus: String? = null,
    val productIsActive: Boolean? = null,
    val productIsRemoved: Boolean? = null,

    // User 필드 (주문자 정보)
    val userId: Int? = null,
    val userEmail: String? = null,
    val userIsActive: Boolean? = null,
)