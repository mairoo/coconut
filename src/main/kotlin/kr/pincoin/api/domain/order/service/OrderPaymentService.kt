package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.error.OrderPaymentErrorCode
import kr.pincoin.api.domain.order.model.OrderPayment
import kr.pincoin.api.domain.order.repository.OrderPaymentRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderPaymentService(
    private val orderPaymentRepository: OrderPaymentRepository,
) {
    @Transactional
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment =
        try {
            orderPaymentRepository.save(orderPayment)
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(OrderPaymentErrorCode.ALREADY_EXISTS)
        }

    fun get(
        id: Long,
    ): OrderPayment =
        orderPaymentRepository.findById(id)
            ?: throw BusinessException(OrderPaymentErrorCode.NOT_FOUND)

    fun get(
        paymentId: Long,
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment =
        orderPaymentRepository.findOrderPayment(paymentId, criteria)
            ?: throw BusinessException(OrderPaymentErrorCode.NOT_FOUND)

    fun get(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment =
        orderPaymentRepository.findOrderPayment(criteria)
            ?: throw BusinessException(OrderPaymentErrorCode.NOT_FOUND)

    fun find(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPayment> =
        orderPaymentRepository.findOrderPayments(criteria, pageable)
}