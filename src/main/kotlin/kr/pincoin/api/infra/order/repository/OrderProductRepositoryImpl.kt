package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.repository.OrderProductRepository
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl(
    private val jpaRepository: OrderProductJpaRepository,
    private val queryRepository: OrderProductQueryRepository,
) : OrderProductRepository {
}