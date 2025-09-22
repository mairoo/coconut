package kr.pincoin.api.infra.order.repository.criteria

import java.math.BigDecimal
import java.time.LocalDateTime

data class OrderPaymentSearchCriteria(
    // OrderPayment 필드
    val paymentId: Long? = null,
    val orderId: Long? = null,
    val account: Int? = null,
    val amount: BigDecimal? = null,
    val balance: BigDecimal? = null,
    val received: LocalDateTime? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val isActive: Boolean? = null,
    val isRemoved: Boolean? = null,

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