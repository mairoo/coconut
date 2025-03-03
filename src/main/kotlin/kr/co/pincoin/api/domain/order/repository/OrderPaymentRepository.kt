package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface OrderPaymentRepository {
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment

    fun saveAll(
        orderPayments: List<OrderPayment>,
    ): List<OrderPayment>

    fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment?

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
    ): List<OrderPayment>

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPayment>
}