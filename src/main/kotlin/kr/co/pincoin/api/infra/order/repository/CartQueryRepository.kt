package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.CartEntity
import kr.co.pincoin.api.infra.order.repository.criteria.CartSearchCriteria

interface CartQueryRepository {
    fun findCart(
        criteria: CartSearchCriteria,
    ): CartEntity?
}