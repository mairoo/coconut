package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderRepository {
    fun save(
        order: Order,
    ): Order

    fun saveAndFlush(
        order: Order,
    ): Order

    fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order?

    fun findOrderWithUserProfile(
        criteria: OrderSearchCriteria,
    ): OrderUserProfileProjection?

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<Order>

    fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<Order>

    fun findOrdersWithUserProfile(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection>
}