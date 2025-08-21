package kr.pincoin.api.app.order.admin.request

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.LocalDateTime

data class AdminOrderSearchRequest(
    // Order 필드
    @field:JsonProperty("orderId")
    val orderId: Long? = null,

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

    @field:JsonProperty("isActive")
    val isActive: Boolean? = null,

    @field:JsonProperty("isRemoved")
    val isRemoved: Boolean? = null,

    // User 필드 (주문자 정보)
    @field:JsonProperty("userId")
    val userId: Long? = null,

    @field:JsonProperty("userEmail")
    val userEmail: String? = null,

    @field:JsonProperty("userIsActive")
    val userIsActive: Boolean? = null,

    @field:JsonProperty("userIsRemoved")
    val userIsRemoved: Boolean? = null,
)