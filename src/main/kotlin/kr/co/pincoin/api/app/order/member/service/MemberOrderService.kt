package kr.co.pincoin.api.app.order.member.service

import jakarta.servlet.http.HttpServletRequest
import kr.co.pincoin.api.app.order.member.request.CartOrderCreateRequest
import kr.co.pincoin.api.app.order.member.request.OrderSearchRequest
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.service.OrderService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MemberOrderService(
    private val orderService: OrderService,
) {
    fun getOrder(
        userId: Int,
        id: Long,
        request: OrderSearchRequest,
    ): Order =
        orderService.findOrder(
            OrderSearchCriteria(
                userId = userId,
                id = id,
                orderNo = request.orderNo,
                paymentMethod = request.paymentMethod,
                transactionId = request.transactionId,
                status = request.status,
                visible = OrderVisibility.VISIBLE,
                currency = request.currency,
                suspicious = false,
                isRemoved = false,
            )
        ) ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    fun getOrders(
        userId: Int,
        request: OrderSearchRequest,
        pageable: Pageable,
    ): Page<Order> =
        orderService.findOrders(
            OrderSearchCriteria(
                userId = userId,
                id = request.orderId,
                orderNo = request.orderNo,
                paymentMethod = request.paymentMethod,
                transactionId = request.transactionId,
                status = request.status,
                visible = OrderVisibility.VISIBLE,
                currency = request.currency,
                suspicious = false,
                isRemoved = false,
            ),
            pageable
        )

    fun createOrder(
        userId: Int,
        request: CartOrderCreateRequest,
        servletRequest: HttpServletRequest,
    ): Order =
        orderService.createOrderFromCart(userId, request, servletRequest)
}