package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.OrderProductVoucher
import kr.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.pincoin.api.infra.order.mapper.toEntity
import kr.pincoin.api.infra.order.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherRepositoryImpl(
    private val jpaRepository: OrderProductVoucherJpaRepository,
    private val queryRepository: OrderProductVoucherQueryRepository,
) : OrderProductVoucherRepository {
    override fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher =
        orderProductVoucher.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문발송상품권 저장 실패")

    override fun findById(
        id: Long,
    ): OrderProductVoucher? =
        queryRepository.findById(id)?.toModel()
}