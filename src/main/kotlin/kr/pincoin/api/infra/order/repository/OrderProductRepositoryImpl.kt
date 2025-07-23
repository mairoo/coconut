package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.domain.order.repository.OrderProductRepository
import kr.pincoin.api.infra.order.mapper.toEntity
import kr.pincoin.api.infra.order.mapper.toModel
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
}