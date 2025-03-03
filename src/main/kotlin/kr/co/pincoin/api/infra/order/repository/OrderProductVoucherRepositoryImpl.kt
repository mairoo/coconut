package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherRepositoryImpl(
    private val jpaRepository: OrderProductVoucherJpaRepository,
    private val jdbcRepository: OrderProductVoucherJdbcRepository,
    private val queryRepository: OrderProductVoucherQueryRepository,
) : OrderProductVoucherRepository {
    override fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher =
        orderProductVoucher.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문발권상품권 저장 실패")

    override fun saveAll(
        orderProductVouchers: List<OrderProductVoucher>,
    ): List<OrderProductVoucher> {
        if (orderProductVouchers.isEmpty()) return emptyList()

        val (existingOrderProductVouchers, newOrderProductVouchers) = orderProductVouchers.partition { it.id != null }

        if (existingOrderProductVouchers.isNotEmpty()) {
            jdbcRepository.batchUpdate(existingOrderProductVouchers)
        }

        if (newOrderProductVouchers.isNotEmpty()) {
            jdbcRepository.batchInsert(newOrderProductVouchers)
        }

        return orderProductVouchers
    }

    override fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher? =
        queryRepository.findOrderProductVoucher(criteria)?.toModel()

    override fun findOrderProductVouchersWithProduct(
        criteria: OrderProductVoucherSearchCriteria,
    ): List<OrderProductVoucherProjection> =
        queryRepository.findOrderProductVouchersWithProduct(criteria)
}