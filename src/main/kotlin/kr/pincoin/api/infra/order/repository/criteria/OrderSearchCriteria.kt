package kr.pincoin.api.infra.order.repository.criteria

import kr.pincoin.api.app.order.admin.request.AdminOrderSearchRequest
import kr.pincoin.api.app.order.my.request.MyOrderSearchRequest
import java.time.LocalDateTime

data class OrderSearchCriteria(
    // Order 필드
    val orderId: Long? = null,
    val orderNumber: String? = null,
    val status: String? = null,
    val paymentMethod: String? = null,
    val paymentStatus: String? = null,
    val startDateTime: LocalDateTime? = null,
    val endDateTime: LocalDateTime? = null,
    val isActive: Boolean? = null,
    val isRemoved: Boolean? = null,

    // User 필드 (주문자 정보)
    val userId: Int? = null,
    val userEmail: String? = null,
    val userIsActive: Boolean? = null,
    val userIsRemoved: Boolean? = null,
) {
    companion object {
        fun from(
            request: AdminOrderSearchRequest,
        ) = OrderSearchCriteria(
            orderId = request.orderId,
            orderNumber = request.orderNumber,
            status = request.status,
            paymentMethod = request.paymentMethod,
            paymentStatus = request.paymentStatus,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            isActive = request.isActive,
            isRemoved = request.isRemoved,
            userId = request.userId,
            userEmail = request.userEmail,
            userIsActive = request.userIsActive,
            userIsRemoved = request.userIsRemoved,
        )

        fun from(
            request: MyOrderSearchRequest,
        ) = OrderSearchCriteria(
            orderNumber = request.orderNumber,
            status = request.status,
            paymentMethod = request.paymentMethod,
            paymentStatus = request.paymentStatus,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
        )

        fun from(
            request: MyOrderSearchRequest,
            userId: Int,
        ) = OrderSearchCriteria(
            orderNumber = request.orderNumber,
            status = request.status,
            paymentMethod = request.paymentMethod,
            paymentStatus = request.paymentStatus,
            startDateTime = request.startDateTime,
            endDateTime = request.endDateTime,
            userId = userId,
        )
    }
}