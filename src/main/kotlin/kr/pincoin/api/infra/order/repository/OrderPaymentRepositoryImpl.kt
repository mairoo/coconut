package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.repository.OrderPaymentRepository
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentRepositoryImpl(
    private val jpaRepository: OrderPaymentJpaRepository,
    private val queryRepository: OrderPaymentQueryRepository,
) : OrderPaymentRepository {
}