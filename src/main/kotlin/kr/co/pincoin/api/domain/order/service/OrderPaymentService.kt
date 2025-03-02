package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.repository.OrderPaymentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderPaymentService(
    private val orderPaymentRepository: OrderPaymentRepository,
) {
}