package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderProductService(
    private val orderProductRepository: OrderProductRepository,
) {
}