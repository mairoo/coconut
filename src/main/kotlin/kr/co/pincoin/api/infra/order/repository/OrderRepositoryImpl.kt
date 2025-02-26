package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.repository.OrderRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val jpaRepository: OrderJpaRepository,
) : OrderRepository {
    override fun save(
        order: Order,
    ): Order =
        order.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문 저장 실패")
}