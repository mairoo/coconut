package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.repository.OrderPaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderPaymentService(
    private val orderPaymentRepository: OrderPaymentRepository,
) {
}