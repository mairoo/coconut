package kr.pincoin.api.domain.coordinator.order

import kr.pincoin.api.domain.inventory.service.VoucherService
import kr.pincoin.api.domain.order.service.OrderProductService
import kr.pincoin.api.domain.order.service.OrderService
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderResourceCoordinator(
    private val orderService: OrderService,
    private val orderProductService: OrderProductService,
    private val orderProductVoucherService: VoucherService,
) {
    // 주문 생성
}