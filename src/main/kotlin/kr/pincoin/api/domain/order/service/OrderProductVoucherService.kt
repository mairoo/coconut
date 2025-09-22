package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderProductVoucherService(
    private val orderProductVoucherRepository: OrderProductVoucherRepository,
) {
}