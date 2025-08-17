package kr.pincoin.api.infra.order.entity

import jakarta.persistence.*
import kr.pincoin.api.domain.order.enums.OrderCurrency
import kr.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.pincoin.api.domain.order.enums.OrderStatus
import kr.pincoin.api.domain.order.enums.OrderVisible
import kr.pincoin.api.infra.common.jpa.DateTimeFields
import kr.pincoin.api.infra.common.jpa.RemovalFields
import kr.pincoin.api.infra.order.converter.OrderCurrencyConverter
import kr.pincoin.api.infra.order.converter.OrderPaymentMethodConverter
import kr.pincoin.api.infra.order.converter.OrderStatusConverter
import kr.pincoin.api.infra.order.converter.OrderVisibleConverter
import org.hibernate.annotations.JdbcTypeCode
import org.hibernate.type.SqlTypes
import java.math.BigDecimal
import java.util.*

@Entity
@Table(name = "shop_order")
class OrderEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Embedded
    val removalFields: RemovalFields = RemovalFields(),

    @Column(name = "order_no")
    val orderNo: UUID,

    @Column(name = "user_agent")
    val userAgent: String,

    @Column(name = "accept_language")
    val acceptLanguage: String,

    @Column(name = "ip_address", columnDefinition = "inet")
    @JdbcTypeCode(SqlTypes.INET)
    val ipAddress: String,

    @Column(name = "payment_method")
    @Convert(converter = OrderPaymentMethodConverter::class)
    val paymentMethod: OrderPaymentMethod,

    @Column(name = "status")
    @Convert(converter = OrderStatusConverter::class)
    val status: OrderStatus,

    @Column(name = "total_list_price")
    val totalListPrice: BigDecimal,

    @Column(name = "total_selling_price")
    val totalSellingPrice: BigDecimal,

    @Column(name = "currency")
    @Convert(converter = OrderCurrencyConverter::class)
    val currency: OrderCurrency,

    @Column(name = "message")
    val message: String,

    @Column(name = "parent_id")
    val parentId: Long?,

    @Column(name = "user_id")
    val userId: Int?,

    @Column(name = "fullname")
    val fullname: String,

    @Column(name = "transaction_id")
    val transactionId: String,

    @Column(name = "visible")
    @Convert(converter = OrderVisibleConverter::class)
    val visible: OrderVisible,

    @Column(name = "suspicious")
    val suspicious: Boolean,
) {
    companion object {
        fun of(
            id: Long? = null,
            orderNo: UUID = UUID.randomUUID(),
            userAgent: String = "",
            acceptLanguage: String = "",
            ipAddress: String,
            paymentMethod: OrderPaymentMethod = OrderPaymentMethod.BANK_TRANSFER,
            status: OrderStatus = OrderStatus.PAYMENT_PENDING,
            totalListPrice: BigDecimal = BigDecimal.ZERO,
            totalSellingPrice: BigDecimal = BigDecimal.ZERO,
            currency: OrderCurrency = OrderCurrency.KRW,
            message: String = "",
            parentId: Long? = null,
            userId: Int? = null,
            fullname: String,
            transactionId: String = "",
            visible: OrderVisible = OrderVisible.VISIBLE,
            suspicious: Boolean = false,
            isRemoved: Boolean = false,
        ) = OrderEntity(
            id = id,
            removalFields = RemovalFields().apply {
                this.isRemoved = isRemoved
            },
            orderNo = orderNo,
            userAgent = userAgent,
            acceptLanguage = acceptLanguage,
            ipAddress = ipAddress,
            paymentMethod = paymentMethod,
            status = status,
            totalListPrice = totalListPrice,
            totalSellingPrice = totalSellingPrice,
            currency = currency,
            message = message,
            parentId = parentId,
            userId = userId,
            fullname = fullname,
            transactionId = transactionId,
            visible = visible,
            suspicious = suspicious,
        )
    }
}