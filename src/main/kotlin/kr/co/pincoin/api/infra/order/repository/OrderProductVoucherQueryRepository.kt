package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductVoucherQueryRepository {
    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity?

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
    ): List<OrderProductVoucherEntity>

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherEntity>

    fun findOrderProductVouchersWithProduct(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherProjection>
}