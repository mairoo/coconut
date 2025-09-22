package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderPaymentEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderPaymentQueryRepository {
    fun findById(
        id: Long,
    ): OrderPaymentEntity?

    fun findOrderPayment(
        paymentId: Long,
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity?

    fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity?

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPaymentEntity>
}