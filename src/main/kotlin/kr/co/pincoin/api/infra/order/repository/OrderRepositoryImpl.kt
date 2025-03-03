package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.repository.OrderRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import kr.co.pincoin.api.infra.order.mapper.toModelList
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
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

    override fun saveAndFlush(
        order: Order,
    ): Order =
        order.toEntity()
            ?.let { jpaRepository.saveAndFlush(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문 저장 실패")

    override fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order? =
        queryRepository.findOrder(criteria)?.toModel()

    override fun findOrderWithUserProfile(
        criteria: OrderSearchCriteria,
    ): OrderUserProfileProjection? =
        queryRepository.findOrderWithUserProfile(criteria)

    override fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<Order> =
        queryRepository.findOrders(criteria).toModelList()

    override fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<Order> =
        queryRepository.findOrders(criteria, pageable).map { it.toModel() }

    override fun findOrdersWithUserProfile(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection> =
        queryRepository.findOrdersWithUserProfile(criteria, pageable)
}