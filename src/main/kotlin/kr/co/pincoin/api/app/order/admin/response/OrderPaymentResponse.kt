package kr.co.pincoin.api.app.order.admin.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import kr.co.pincoin.api.domain.order.model.OrderPayment
import java.math.BigDecimal
import java.time.ZonedDateTime

@JsonInclude(JsonInclude.Include.NON_NULL)
data class OrderPaymentResponse(
    @JsonProperty("id")
    val id: Long?,

    @JsonProperty("created")
    val created: ZonedDateTime?,

    @JsonProperty("modified")
    val modified: ZonedDateTime?,

    @JsonProperty("isRemoved")
    val isRemoved: Boolean,

    @JsonProperty("orderId")
    val orderId: Long,

    @JsonProperty("amount")
    val amount: BigDecimal,

    @JsonProperty("received")
    val received: ZonedDateTime,

    @JsonProperty("account")
    val account: PaymentBankAccount,

    @JsonProperty("balance")
    val balance: BigDecimal
) {
    companion object {
        fun from(orderPayment: OrderPayment): OrderPaymentResponse =
            with(orderPayment) {
                OrderPaymentResponse(
                    id = id,
                    created = created,
                    modified = modified,
                    isRemoved = isRemoved,
                    orderId = orderId,
                    amount = amount,
                    received = received,
                    account = account,
                    balance = balance
                )
            }
    }
}