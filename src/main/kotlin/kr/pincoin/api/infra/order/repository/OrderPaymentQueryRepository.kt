package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderPaymentEntity

interface OrderPaymentQueryRepository {
    fun findById(
        id: Long,
    ): OrderPaymentEntity?
}