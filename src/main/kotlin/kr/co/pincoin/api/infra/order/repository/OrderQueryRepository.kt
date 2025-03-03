package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderQueryRepository {
    fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity?

    fun findOrderWithUserProfile(
        criteria: OrderSearchCriteria,
    ): OrderUserProfileProjection?

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<OrderEntity>

    fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderEntity>

    fun findOrdersWithUserProfile(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection>
}