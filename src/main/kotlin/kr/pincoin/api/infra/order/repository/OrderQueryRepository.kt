package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderQueryRepository {
    fun findById(
        id: Long,
    ): OrderEntity?

    fun findOrder(
        orderId: Long,
        criteria: OrderSearchCriteria,
    ): OrderEntity?

    fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity?

    fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderEntity>
}