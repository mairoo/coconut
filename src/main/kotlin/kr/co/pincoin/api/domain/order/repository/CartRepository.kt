package kr.co.pincoin.api.domain.order.repository

import kr.co.pincoin.api.domain.order.model.Cart
import kr.co.pincoin.api.infra.order.repository.criteria.CartSearchCriteria

interface CartRepository {
    fun save(cart: Cart): Cart

    fun findCart(criteria: CartSearchCriteria): Cart?
}
