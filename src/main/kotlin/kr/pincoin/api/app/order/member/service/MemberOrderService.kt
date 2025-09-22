package kr.pincoin.api.app.order.member.service

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.order.member.request.MemberOrderCreateRequest
import kr.pincoin.api.domain.coordinator.order.OrderResourceCoordinator
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
// @PreAuthorize("isAuthenticated() and hasRole('USER')")
@PreAuthorize("isAuthenticated()")
class MemberOrderService(
    private val orderResourceCoordinator: OrderResourceCoordinator,
) {
    fun createOrder(userId: Int, request: MemberOrderCreateRequest, httpRequest: HttpServletRequest) =
        orderResourceCoordinator.createOrder(userId, request, httpRequest)
}