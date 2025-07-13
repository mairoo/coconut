package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderProductEntity

interface OrderProductQueryRepository {
    fun findById(
        id: Long,
    ): OrderProductEntity?
}