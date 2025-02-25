package kr.co.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields
import kr.co.pincoin.api.infra.common.jpa.RemovalFields
import kr.co.pincoin.api.infra.order.converter.OrderCurrencyConverter
import kr.co.pincoin.api.infra.order.converter.OrderPaymentMethodConverter
import kr.co.pincoin.api.infra.order.converter.OrderStatusConverter
import kr.co.pincoin.api.infra.order.converter.OrderVisibilityConverter

import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "shop_order")
class OrderEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "order_no")
    val orderNo: UUID = UUID.randomUUID(),

    @Column(name = "user_id", columnDefinition = "int4")
    val userId: Long? = null,

    @Column(name = "fullname")
    val fullname: String,

    @Column(name = "user_agent")
    val userAgent: String,

    @Column(name = "accept_language")
    val acceptLanguage: String,

    @Column(name = "ip_address", columnDefinition = "inet")
    val ipAddress: String,

    @Column(name = "payment_method")
    @Convert(converter = OrderPaymentMethodConverter::class)
    val paymentMethod: OrderPaymentMethod = OrderPaymentMethod.BANK_TRANSFER,

    @Column(name = "transaction_id")
    val transactionId: String,

    @Column(name = "status")
    @Convert(converter = OrderStatusConverter::class)
    val status: OrderStatus = OrderStatus.PAYMENT_PENDING,

    @Column(name = "visible")
    @Convert(converter = OrderVisibilityConverter::class)
    val visible: OrderVisibility = OrderVisibility.VISIBLE,

    @Column(name = "total_list_price", precision = 11, scale = 2)
    val totalListPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "total_selling_price", precision = 11, scale = 2)
    val totalSellingPrice: BigDecimal = BigDecimal.ZERO,

    @Column(name = "currency")
    @Convert(converter = OrderCurrencyConverter::class)
    val currency: OrderCurrency = OrderCurrency.KRW,

    @Column(name = "message")
    val message: String,

    @Column(name = "parent_id")
    val parentId: Long? = null,

    @Column(name = "suspicious")
    val suspicious: Boolean = false,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            isRemoved: Boolean = false,
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
        ) = OrderEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
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