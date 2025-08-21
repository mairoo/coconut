package kr.pincoin.api.app.order.admin.service

import kr.pincoin.api.app.order.admin.request.AdminOrderSearchRequest
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.service.OrderService
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminOrderService(
    private val orderService: OrderService,
) {
    fun getOrder(
        orderId: Long,
        request: AdminOrderSearchRequest,
    ): Order =
        orderService.findOrder(
            orderId,
            OrderSearchCriteria.from(request),
        )
}