package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.model.Cart
import kr.co.pincoin.api.domain.order.repository.CartRepository
import kr.co.pincoin.api.infra.order.mapper.toEntity
import kr.co.pincoin.api.infra.order.mapper.toModel
import kr.co.pincoin.api.infra.order.repository.criteria.CartSearchCriteria
import org.springframework.stereotype.Repository

@Repository
class CartRepositoryImpl(
    private val jpaRepository: CartJpaRepository,
    private val queryRepository: CartQueryRepository,
) : CartRepository {
    override fun save(
        cart: Cart,
    ): Cart =
        cart
            .toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("장바구니 저장 실패")

    override fun findCart(
        criteria: CartSearchCriteria,
    ): Cart? =
        queryRepository.findCart(criteria)?.toModel()
}