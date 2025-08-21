package kr.pincoin.api.app.order.member.service

import kr.pincoin.api.domain.order.service.OrderService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated() and hasRole('USER')")
class MemberOrderService(
    private val orderService: OrderService,
) {
}