package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity

interface OrderProductVoucherQueryRepository {
    fun findById(
        id: Long,
    ): OrderProductVoucherEntity?
}