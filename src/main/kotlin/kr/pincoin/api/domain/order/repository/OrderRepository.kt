package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.Order

interface OrderRepository {
    fun save(
        order: Order,
    ): Order

    fun findById(
        id: Long,
    ): Order?
}