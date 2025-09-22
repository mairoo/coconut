package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderProductVoucher
import kr.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductVoucherRepository {
    fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher

    fun findById(
        id: Long,
    ): OrderProductVoucher?

    fun findOrderProductVoucher(
        voucherId: Long,
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher?

    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher?

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucher>
}