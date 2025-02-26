package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class OrderProductRepositoryImpl(
    private val jpaRepository: OrderProductJpaRepository,
) : OrderProductRepository {
    override fun save(
        orderProduct: OrderProduct,
    ): OrderProduct =
        orderProduct.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("주문상품 저장 실패")
}