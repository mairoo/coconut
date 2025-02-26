package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.OrderPayment

interface OrderPaymentRepository {
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment
}