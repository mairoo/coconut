package kr.co.pincoin.api.app.order.member.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.model.Order
import java.math.BigDecimal
import java.net.InetAddress
import java.time.ZonedDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderResponse(
    @JsonProperty("id")
    val id: Long,

    @JsonProperty("created")
    val created: ZonedDateTime,

    @JsonProperty("modified")
    val modified: ZonedDateTime,

    @JsonProperty("orderNo")
    val orderNo: UUID,

    @JsonProperty("userId")
    val userId: Int?,

    @JsonProperty("fullname")
    val fullname: String,

    @JsonProperty("userAgent")
    val userAgent: String,

    @JsonProperty("acceptLanguage")
    val acceptLanguage: String,

    @JsonProperty("ipAddress")
    val ipAddress: InetAddress,

    @JsonProperty("paymentMethod")
    val paymentMethod: OrderPaymentMethod,

    @JsonProperty("transactionId")
    val transactionId: String,

    @JsonProperty("status")
    val status: OrderStatus,

    @JsonProperty("totalListPrice")
    val totalListPrice: BigDecimal,

    @JsonProperty("totalSellingPrice")
    val totalSellingPrice: BigDecimal,

    @JsonProperty("currency")
    val currency: OrderCurrency,

    @JsonProperty("message")
    val message: String,

    @JsonProperty("parentId")
    val parentId: Long?,
) {
    companion object {
        fun from(order: Order): OrderResponse = with(order) {
            OrderResponse(
                id = id!!,
                created = created!!,
                modified = modified!!,
                orderNo = orderNo,
                userId = userId,
                fullname = fullname,
                userAgent = userAgent,
                acceptLanguage = acceptLanguage,
                ipAddress = ipAddress,
                paymentMethod = paymentMethod,
                transactionId = transactionId,
                status = status,
                totalListPrice = totalListPrice,
                totalSellingPrice = totalSellingPrice,
                currency = currency,
                message = message,
                parentId = parentId,
            )
        }
    }
}