package kr.pincoin.api.app.order.member.request

import com.fasterxml.jackson.annotation.JsonProperty
import kr.pincoin.api.domain.order.enums.OrderPaymentMethod
import java.math.BigDecimal

data class MemberOrderCreateRequest(
    @field:JsonProperty("paymentMethod")
    val paymentMethod: OrderPaymentMethod,

    @field:JsonProperty("products")
    val products: List<MemberOrderProductRequest>,

    @field:JsonProperty("totalAmount")
    val totalAmount: BigDecimal,

    @field:JsonProperty("productCount")
    val productCount: Int,
)