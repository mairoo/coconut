package kr.pincoin.api.app.order.my.service

import kr.pincoin.api.app.order.my.request.MyOrderSearchRequest
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.service.OrderService
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@PreAuthorize("isAuthenticated()")
@Transactional(readOnly = true)
class MyOrderService(
    private val orderService: OrderService,
) {
    fun getOrder(
        orderId: Long,
        userId: Int,
        request: MyOrderSearchRequest,
    ): Order =
        orderService.get(
            orderId,
            OrderSearchCriteria.from(request, userId),
        )

    fun getOrders(
        request: MyOrderSearchRequest,
        userId: Int,
        pageable: Pageable,
    ): Page<Order> =
        orderService.find(
            OrderSearchCriteria.from(request, userId),
            pageable,
        )
}