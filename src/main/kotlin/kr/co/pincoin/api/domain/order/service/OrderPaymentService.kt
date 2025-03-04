package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.domain.order.repository.OrderPaymentRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class OrderPaymentService(
    private val orderPaymentRepository: OrderPaymentRepository,
) {
    @Transactional
    fun save(
        orderPayment: OrderPayment,
    ): OrderPayment {
        try {
            return orderPaymentRepository.save(orderPayment)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(OrderErrorCode.ORDER_PAYMENT_SAVE_FAILED)
        }
    }

    @Transactional
    fun saveAll(
        orderPayments: List<OrderPayment>,
    ): List<OrderPayment> =
        orderPaymentRepository.saveAll(orderPayments)

    fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment? = orderPaymentRepository.findOrderPayment(criteria)

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
    ): List<OrderPayment> = orderPaymentRepository.findOrderPayments(criteria)

    fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPayment> = orderPaymentRepository.findOrderPayments(criteria, pageable)

    @Transactional
    fun updateAccount(
        id: Long,
        account: PaymentBankAccount,
    ): OrderPayment =
        orderPaymentRepository.findOrderPayment(
            OrderPaymentSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateAccount(account)
            ?.let { orderPaymentRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PAYMENT_NOT_FOUND)

    @Transactional
    fun updateBalance(
        id: Long,
        balance: BigDecimal,
    ): OrderPayment =
        orderPaymentRepository.findOrderPayment(
            OrderPaymentSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateBalance(balance)
            ?.let { orderPaymentRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PAYMENT_NOT_FOUND)

    @Transactional
    fun removeOrderPayment(
        id: Long,
    ): OrderPayment =
        orderPaymentRepository.findOrderPayment(
            OrderPaymentSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsRemoved()
            ?.let { orderPaymentRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PAYMENT_NOT_FOUND)

    @Transactional
    fun restoreOrderPayment(
        id: Long,
    ): OrderPayment {
        val orderPayment = orderPaymentRepository.findOrderPayment(
            OrderPaymentSearchCriteria(
                id = id,
                isRemoved = true
            )
        ) ?: throw BusinessException(OrderErrorCode.ORDER_PAYMENT_NOT_FOUND)

        val restoredOrderPayment = OrderPayment.of(
            id = orderPayment.id,
            created = orderPayment.created,
            modified = orderPayment.modified,
            isRemoved = false,
            orderId = orderPayment.orderId,
            account = orderPayment.account,
            amount = orderPayment.amount,
            balance = orderPayment.balance,
            received = orderPayment.received,
        )

        return save(restoredOrderPayment)
    }

    @Transactional
    fun removeOrderPayments(
        ids: List<Long>,
    ): List<OrderPayment> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        val orderPayments = ids.mapNotNull { id ->
            orderPaymentRepository.findOrderPayment(
                OrderPaymentSearchCriteria(id = id, isRemoved = false)
            )
        }

        if (orderPayments.isEmpty()) {
            return emptyList()
        }

        val removedOrderPayments = orderPayments.map { it.markAsRemoved() }
        return saveAll(removedOrderPayments)
    }

    @Transactional
    fun removeOrderPaymentsByOrderId(
        orderId: Long,
    ): List<OrderPayment> {
        val orderPayments = orderPaymentRepository.findOrderPayments(
            OrderPaymentSearchCriteria(
                orderId = orderId,
                isRemoved = false
            )
        )

        if (orderPayments.isEmpty()) {
            return emptyList()
        }

        val removedOrderPayments = orderPayments.map { it.markAsRemoved() }
        return saveAll(removedOrderPayments)
    }
}