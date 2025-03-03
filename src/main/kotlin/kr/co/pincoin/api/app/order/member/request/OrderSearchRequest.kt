package kr.co.pincoin.api.app.order.member.request

import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import java.util.*

data class OrderSearchRequest(
    val orderId: Long? = null,
    val orderNo: UUID? = null,
    val paymentMethod: OrderPaymentMethod? = null,
    val transactionId: String? = null,
    val status: OrderStatus? = null,
    val currency: OrderCurrency? = null,
)