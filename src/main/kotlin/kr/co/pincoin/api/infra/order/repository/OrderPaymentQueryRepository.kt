package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderPaymentEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderPaymentQueryRepository {
    fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity?

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
    ): List<OrderPaymentEntity>

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPaymentEntity>
}