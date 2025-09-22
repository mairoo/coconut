package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.domain.order.repository.OrderProductRepository
import kr.pincoin.api.infra.order.mapper.toEntity
import kr.pincoin.api.infra.order.mapper.toModel
import kr.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl(
    private val jpaRepository: OrderProductJpaRepository,
    private val queryRepository: OrderProductQueryRepository,
) : OrderProductRepository {
    override fun save(
        orderProduct: OrderProduct,
    ): OrderProduct =
        orderProduct.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문항목 저장 실패")

    override fun findById(
        id: Long,
    ): OrderProduct? =
        queryRepository.findById(id)?.toModel()

    override fun findOrderProduct(
        orderProductId: Long,
        criteria: OrderProductSearchCriteria,
    ): OrderProduct? =
        queryRepository.findOrderProduct(orderProductId, criteria)?.toModel()

    override fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct? =
        queryRepository.findOrderProduct(criteria)?.toModel()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProduct> =
        queryRepository.findOrderProducts(criteria, pageable).map { it.toModel() }
}