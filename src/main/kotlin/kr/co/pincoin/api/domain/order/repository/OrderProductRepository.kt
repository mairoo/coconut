package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductRepository {
    fun save(
        orderProduct: OrderProduct,
    ): OrderProduct

    fun saveAll(
        orderProducts: List<OrderProduct>,
    ): List<OrderProduct>

    fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct?

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
    ): List<OrderProduct>


    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProduct>
}