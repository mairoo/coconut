package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl(
    private val jpaRepository: OrderProductJpaRepository,
) : OrderProductRepository {
}