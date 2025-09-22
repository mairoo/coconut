package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductRepository {
    fun save(
        orderProduct: OrderProduct,
    ): OrderProduct

    fun findById(
        id: Long,
    ): OrderProduct?

    fun findOrderProduct(
        orderProductId: Long,
        criteria: OrderProductSearchCriteria,
    ): OrderProduct?

    fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct?

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProduct>
}