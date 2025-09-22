package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.repository.OrderProductRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class OrderProductService(
    private val orderProductRepository: OrderProductRepository,
) {
}