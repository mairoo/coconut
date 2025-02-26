package kr.co.pincoin.api.domain.order.model

import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

class Order private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val orderNo: UUID,
    val ipAddress: String,

    // 4. 도메인 로직 가변 필드
    userId: Long?,
    fullname: String,
    userAgent: String,
    acceptLanguage: String,
    paymentMethod: OrderPaymentMethod,
    transactionId: String,
    status: OrderStatus,
    visible: OrderVisibility,
    totalListPrice: BigDecimal,
    totalSellingPrice: BigDecimal,
    currency: OrderCurrency,
    message: String,
    parentId: Long?,
    suspicious: Boolean,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var userId: Long? = userId
        private set

    var fullname: String = fullname
        private set

    var userAgent: String = userAgent
        private set

    var acceptLanguage: String = acceptLanguage
        private set

    var paymentMethod: OrderPaymentMethod = paymentMethod
        private set

    var transactionId: String = transactionId
        private set

    var status: OrderStatus = status
        private set

    var visible: OrderVisibility = visible
        private set

    var totalListPrice: BigDecimal = totalListPrice
        private set

    var totalSellingPrice: BigDecimal = totalSellingPrice
        private set

    var currency: OrderCurrency = currency
        private set

    var message: String = message
        private set

    var parentId: Long? = parentId
        private set

    var suspicious: Boolean = suspicious
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderNo: UUID = UUID.randomUUID(),
            userId: Long? = null,
            fullname: String = "",
            userAgent: String = "",
            acceptLanguage: String = "",
            ipAddress: String,
            paymentMethod: OrderPaymentMethod = OrderPaymentMethod.BANK_TRANSFER,
            transactionId: String = "",
            status: OrderStatus = OrderStatus.PAYMENT_PENDING,
            visible: OrderVisibility = OrderVisibility.VISIBLE,
            totalListPrice: BigDecimal = BigDecimal.ZERO,
            totalSellingPrice: BigDecimal = BigDecimal.ZERO,
            currency: OrderCurrency = OrderCurrency.KRW,
            message: String = "",
            parentId: Long? = null,
            suspicious: Boolean = false,
        ): Order =
            Order(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                orderNo = orderNo,
                userId = userId,
                fullname = fullname,
                userAgent = userAgent,
                acceptLanguage = acceptLanguage,
                ipAddress = ipAddress,
                paymentMethod = paymentMethod,
                transactionId = transactionId,
                status = status,
                visible = visible,
                totalListPrice = totalListPrice,
                totalSellingPrice = totalSellingPrice,
                currency = currency,
                message = message,
                parentId = parentId,
                suspicious = suspicious,
            )
    }
}