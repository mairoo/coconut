package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductVoucherRepository {
    fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher

    fun saveAll(
        orderProductVouchers: List<OrderProductVoucher>,
    ): List<OrderProductVoucher>

    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher?

    fun findOrderProductVouchersWithProduct(
        criteria: OrderProductVoucherSearchCriteria,
    ): List<OrderProductVoucherProjection>

    fun findOrderProductVouchersWithProduct(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherProjection>
}