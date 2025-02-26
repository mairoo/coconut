package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.domain.order.repository.OrderPaymentRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentRepositoryImpl(
    private val jpaRepository: OrderPaymentJpaRepository,
) : OrderPaymentRepository {
    override fun save(
        orderPayment: OrderPayment,
    ): OrderPayment =
        orderPayment.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문결제 저장 실패")
}