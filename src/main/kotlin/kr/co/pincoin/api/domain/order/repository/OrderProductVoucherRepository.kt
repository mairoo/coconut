package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductVoucherRepository {
    fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher

    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher?

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
    ): List<OrderProductVoucher>

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucher>
}