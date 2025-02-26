package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.repository.OrderPaymentRepository
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentRepositoryImpl(
    private val jpaRepository: OrderPaymentJpaRepository,
) : OrderPaymentRepository {
}