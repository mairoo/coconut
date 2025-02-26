package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherRepositoryImpl(
    private val jpaRepository: OrderProductVoucherJpaRepository,
) : OrderProductVoucherRepository {
    override fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher =
        orderProductVoucher.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문발권상품권 저장 실패")
}