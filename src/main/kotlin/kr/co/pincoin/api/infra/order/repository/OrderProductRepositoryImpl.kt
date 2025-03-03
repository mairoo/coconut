package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import kr.co.pincoin.api.infra.order.mapper.toModelList
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl(
    private val jpaRepository: OrderProductJpaRepository,
    private val jdbcRepository: OrderProductJdbcRepository,
    private val queryRepository: OrderProductQueryRepository,
) : OrderProductRepository {
    override fun save(
        orderProduct: OrderProduct,
    ): OrderProduct =
        orderProduct.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문상품 저장 실패")

    override fun saveAll(
        orderProducts: List<OrderProduct>,
    ): List<OrderProduct> {
        if (orderProducts.isEmpty()) return emptyList()

        val (existingOrderProducts, newOrderProducts) = orderProducts.partition { it.id != null }

        if (existingOrderProducts.isNotEmpty()) {
            jdbcRepository.batchUpdate(existingOrderProducts)
        }

        if (newOrderProducts.isNotEmpty()) {
            jdbcRepository.batchInsert(newOrderProducts)
        }

        return orderProducts
    }

    override fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct? =
        queryRepository.findOrderProduct(criteria)?.toModel()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
    ): List<OrderProduct> =
        queryRepository.findOrderProducts(criteria).toModelList()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable
    ): Page<OrderProduct> =
        queryRepository.findOrderProducts(criteria, pageable).map { it.toModel() }
}