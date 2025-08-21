package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria

interface OrderRepository {
    fun save(
        order: Order,
    ): Order

    fun findById(
        id: Long,
    ): Order?

    fun findOrder(
        orderId: Long,
        criteria: OrderSearchCriteria,
    ): Order?

    fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order?

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<Order>
}