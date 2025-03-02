package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.domain.order.repository.OrderPaymentRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import kr.co.pincoin.api.infra.order.mapper.toModelList
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentRepositoryImpl(
    private val jpaRepository: OrderPaymentJpaRepository,
    private val queryRepository: OrderPaymentQueryRepository,
) : OrderPaymentRepository {
    override fun save(
        orderPayment: OrderPayment,
    ): OrderPayment =
        orderPayment.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문결제 저장 실패")

    override fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPayment? =
        queryRepository.findOrderPayment(criteria)?.toModel()

    override fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
    ): List<OrderPayment> =
        queryRepository.findOrderPayments(criteria).toModelList()

    override fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable
    ): Page<OrderPayment> =
        queryRepository.findOrderPayments(criteria, pageable).map { it.toModel() }
}