package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderProductVoucherQueryRepository {
    fun findById(
        id: Long,
    ): OrderProductVoucherEntity?

    fun findOrderProductVoucher(
        voucherId: Long,
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity?

    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity?

    fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherEntity>
}