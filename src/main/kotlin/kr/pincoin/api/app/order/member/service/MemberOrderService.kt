package kr.pincoin.api.app.order.member.service

import kr.pincoin.api.domain.coordinator.order.OrderResourceCoordinator
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("isAuthenticated() and hasRole('USER')")
class MemberOrderService(
    private val orderResourceCoordinator: OrderResourceCoordinator,
) {
    // 주문 생성
}