package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.OrderProductVoucher
import kr.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.pincoin.api.infra.order.mapper.toEntity
import kr.pincoin.api.infra.order.mapper.toModel
import kr.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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

    override fun findOrderProductVoucher(
        voucherId: Long,
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher? =
        queryRepository.findOrderProductVoucher(voucherId, criteria)?.toModel()

    override fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher? =
        queryRepository.findOrderProductVoucher(criteria)?.toModel()

    override fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucher> =
        queryRepository.findOrderProductVouchers(criteria, pageable).map { it.toModel() }
}