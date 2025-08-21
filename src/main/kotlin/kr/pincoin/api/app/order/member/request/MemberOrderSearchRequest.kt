package kr.pincoin.api.app.order.member.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class MemberOrderSearchRequest(
    @field:JsonProperty("orderNumber")
    val orderNumber: String? = null,

    @field:JsonProperty("status")
    val status: String? = null,

    @field:JsonProperty("paymentMethod")
    val paymentMethod: String? = null,

    @field:JsonProperty("paymentStatus")
    val paymentStatus: String? = null,

    @field:JsonProperty("startDateTime")
    val startDateTime: LocalDateTime? = null,

    @field:JsonProperty("endDateTime")
    val endDateTime: LocalDateTime? = null,
)