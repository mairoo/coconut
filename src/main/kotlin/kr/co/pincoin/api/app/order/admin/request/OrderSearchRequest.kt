package kr.co.pincoin.api.app.order.admin.request

import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import java.net.InetAddress
import java.util.*

data class OrderSearchRequest(
    val orderId: Long? = null,
    val orderNo: UUID? = null,
    val userId: Int? = null,
    val fullname: String? = null,
    val ipAddress: InetAddress? = null,
    val paymentMethod: OrderPaymentMethod? = null,
    val transactionId: String? = null,
    val status: OrderStatus? = null,
    val visible: OrderVisibility? = null,
    val currency: OrderCurrency? = null,
    val parentId: Long? = null,
    val suspicious: Boolean? = null,
    val isRemoved: Boolean? = null,
)