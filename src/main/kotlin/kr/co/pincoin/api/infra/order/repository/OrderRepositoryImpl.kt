package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.repository.OrderRepository
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val jpaRepository: OrderJpaRepository,
) : OrderRepository {
    override fun save(order: Order): Order {
        TODO("Not yet implemented")
    }
}