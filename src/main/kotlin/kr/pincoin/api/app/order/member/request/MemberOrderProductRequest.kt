package kr.pincoin.api.app.order.member.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.math.BigDecimal

data class MemberOrderProductRequest(
    @field:JsonProperty("id")
    val id: String,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("subtitle")
    val subtitle: String,

    @field:JsonProperty("quantity")
    val quantity: Int,

    @field:JsonProperty("price")
    val price: BigDecimal
)