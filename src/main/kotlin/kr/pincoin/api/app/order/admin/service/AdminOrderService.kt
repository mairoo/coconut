package kr.pincoin.api.app.order.admin.service

import kr.pincoin.api.domain.order.service.OrderService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminOrderService(
    private val orderService: OrderService,
) {
}