package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderProductEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductQueryRepository {
    fun findById(
        id: Long,
    ): OrderProductEntity?

    fun findOrderProduct(
        orderProductId: Long,
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity?

    fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity?

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductEntity>
}