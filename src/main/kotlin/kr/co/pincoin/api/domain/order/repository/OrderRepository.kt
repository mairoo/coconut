package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.Order

interface OrderRepository {
    fun save(
        order: Order,
    ): Order
}