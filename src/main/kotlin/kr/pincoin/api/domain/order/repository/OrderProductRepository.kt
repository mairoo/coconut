package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderProduct

interface OrderProductRepository {
    fun save(
        orderProduct: OrderProduct,
    ): OrderProduct
}