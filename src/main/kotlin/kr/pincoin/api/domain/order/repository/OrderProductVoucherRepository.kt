package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderProductVoucher

interface OrderProductVoucherRepository {
    fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher

    fun findById(
        id: Long,
    ): OrderProductVoucher?
}