package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderEntity

interface OrderQueryRepository {
    fun findById(
        id: Long,
    ): OrderEntity?
}