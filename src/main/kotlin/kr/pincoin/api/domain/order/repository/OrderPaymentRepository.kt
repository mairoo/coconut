package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderPayment

interface OrderPaymentRepository {
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment

    fun findById(
        id: Long,
    ): OrderPayment?
}