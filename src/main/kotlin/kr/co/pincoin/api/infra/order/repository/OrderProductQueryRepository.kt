package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderProductEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductQueryRepository {
    fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity?

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
    ): List<OrderProductEntity>


    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductEntity>
}