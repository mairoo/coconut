package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderQueryRepository {
    fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity?

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<OrderEntity>

    fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderEntity>
}