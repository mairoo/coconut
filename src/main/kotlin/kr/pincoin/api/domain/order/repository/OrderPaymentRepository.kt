package kr.pincoin.api.domain.order.repository

import kr.pincoin.api.domain.order.model.OrderPayment
import kr.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderPaymentRepository {
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment

    fun findById(
        id: Long,
    ): OrderPayment?

    fun findOrderPayment(
        paymentId: Long,
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment?

    fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment?

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPayment>
}