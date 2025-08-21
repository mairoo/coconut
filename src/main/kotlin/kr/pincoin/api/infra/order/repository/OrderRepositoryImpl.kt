package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.repository.OrderRepository
import kr.pincoin.api.infra.order.mapper.toEntity
import kr.pincoin.api.infra.order.mapper.toModel
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrderRepositoryImpl(
    private val jpaRepository: OrderJpaRepository,
    private val queryRepository: OrderQueryRepository,
) : OrderRepository {
    override fun save(
        order: Order,
    ): Order =
        order.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문 저장 실패")

    override fun findById(
        id: Long,
    ): Order? =
        queryRepository.findById(id)?.toModel()

    override fun findOrder(
        orderId: Long,
        criteria: OrderSearchCriteria,
    ): Order? =
        queryRepository.findOrder(orderId, criteria)?.toModel()

    override fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order? =
        queryRepository.findOrder(criteria)?.toModel()

    override fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<Order> =
        queryRepository.findOrders(criteria, pageable).map { it.toModel() }
}