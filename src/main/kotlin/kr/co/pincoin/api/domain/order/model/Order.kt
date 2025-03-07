package kr.co.pincoin.api.domain.order.model

import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import java.math.BigDecimal
import java.net.InetAddress
import java.time.ZonedDateTime
import java.util.*

class Order private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val orderNo: UUID,
    val ipAddress: InetAddress,

    // 4. 도메인 로직 가변 필드
    val userId: Int?,
    val fullname: String,
    val userAgent: String,
    val acceptLanguage: String,
    val paymentMethod: OrderPaymentMethod,
    val transactionId: String,
    val status: OrderStatus,
    val visible: OrderVisibility,
    val totalListPrice: BigDecimal,
    val totalSellingPrice: BigDecimal,
    val currency: OrderCurrency,
    val message: String,
    val parentId: Long?,
    val suspicious: Boolean,
) {
    fun updateStatus(newStatus: OrderStatus? = null): Order =
        copy(status = newStatus ?: status)

    fun updateVisibility(newVisible: OrderVisibility? = null): Order =
        copy(visible = newVisible ?: visible)

    fun updateMessage(newMessage: String? = null): Order =
        copy(message = newMessage ?: message)

    fun updateParent(newParentId: Long?): Order =
        copy(parentId = newParentId)

    fun markAsSuspicious(newSuspicious: Boolean = true): Order =
        copy(suspicious = newSuspicious)

    fun markAsRemoved(): Order =
        copy(isRemoved = true)

    private fun copy(
        userId: Int? = this.userId,
        fullname: String? = null,
        userAgent: String? = null,
        acceptLanguage: String? = null,
        paymentMethod: OrderPaymentMethod? = null,
        transactionId: String? = null,
        status: OrderStatus? = null,
        visible: OrderVisibility? = null,
        totalListPrice: BigDecimal? = null,
        totalSellingPrice: BigDecimal? = null,
        currency: OrderCurrency? = null,
        message: String? = null,
        parentId: Long? = this.parentId,
        suspicious: Boolean? = null,
        isRemoved: Boolean? = null
    ): Order = Order(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        orderNo = this.orderNo,
        ipAddress = this.ipAddress,
        userId = userId,
        fullname = fullname ?: this.fullname,
        userAgent = userAgent ?: this.userAgent,
        acceptLanguage = acceptLanguage ?: this.acceptLanguage,
        paymentMethod = paymentMethod ?: this.paymentMethod,
        transactionId = transactionId ?: this.transactionId,
        status = status ?: this.status,
        visible = visible ?: this.visible,
        totalListPrice = totalListPrice ?: this.totalListPrice,
        totalSellingPrice = totalSellingPrice ?: this.totalSellingPrice,
        currency = currency ?: this.currency,
        message = message ?: this.message,
        parentId = parentId,
        suspicious = suspicious ?: this.suspicious
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderNo: UUID = UUID.randomUUID(),
            userId: Int? = null,
            fullname: String = "",
            userAgent: String = "",
            acceptLanguage: String = "",
            ipAddress: InetAddress,
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
                isRemoved = isRemoved ?: false,
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