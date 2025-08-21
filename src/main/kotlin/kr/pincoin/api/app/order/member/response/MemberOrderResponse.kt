package kr.pincoin.api.app.order.member.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.order.enums.OrderCurrency
import kr.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.pincoin.api.domain.order.enums.OrderStatus
import kr.pincoin.api.domain.order.enums.OrderVisible
import kr.pincoin.api.domain.order.model.Order
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MemberOrderResponse(
    @field:JsonProperty("id")
    val id: Long,

    @field:JsonProperty("created")
    val created: LocalDateTime?,

    @field:JsonProperty("modified")
    val modified: LocalDateTime?,

    @field:JsonProperty("orderNo")
    val orderNo: UUID,

    @field:JsonProperty("paymentMethod")
    val paymentMethod: OrderPaymentMethod,

    @field:JsonProperty("status")
    val status: OrderStatus,

    @field:JsonProperty("totalListPrice")
    val totalListPrice: BigDecimal,

    @field:JsonProperty("totalSellingPrice")
    val totalSellingPrice: BigDecimal,

    @field:JsonProperty("currency")
    val currency: OrderCurrency,

    @field:JsonProperty("message")
    val message: String?,

    @field:JsonProperty("fullname")
    val fullname: String,

    @field:JsonProperty("transactionId")
    val transactionId: String?,

    @field:JsonProperty("visible")
    val visible: OrderVisible,

    @field:JsonProperty("suspicious")
    val suspicious: Boolean,
) {
    companion object {
        fun from(order: Order) = with(order) {
            MemberOrderResponse(
                id = id ?: throw IllegalStateException("주문 ID는 필수 입력값입니다"),
                created = created,
                modified = modified,
                orderNo = orderNo,
                paymentMethod = paymentMethod,
                status = status,
                totalListPrice = totalListPrice,
                totalSellingPrice = totalSellingPrice,
                currency = currency,
                message = message.takeIf { it.isNotBlank() },
                fullname = fullname,
                transactionId = transactionId.takeIf { it.isNotBlank() },
                visible = visible,
                suspicious = suspicious,
            )
        }
    }
}