package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.repository.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val jpaRepository: OrderJpaRepository,
    private val queryRepository: OrderQueryRepository,
) : OrderRepository {
}