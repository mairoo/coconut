package kr.co.pincoin.api.domain.order.model

import com.fasterxml.jackson.databind.ObjectMapper

class Cart private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val userId: Int,
    val version: Long,

    // 2. 도메인 로직 가변 필드
    val cartData: String
) {
    fun updateCartData(newCartData: String): Cart {
        validateCartData(newCartData)
        return copy(cartData = newCartData)
    }

    private fun validateCartData(cartData: String) {
        if (cartData.isBlank()) {
            throw IllegalArgumentException("Cart data cannot be empty")
        }
        try {
            ObjectMapper().readTree(cartData)
        } catch (e: Exception) {
            throw IllegalArgumentException("Invalid JSON format in cart data", e)
        }
    }

    private fun copy(
        cartData: String? = null
    ): Cart = Cart(
        id = this.id,
        userId = this.userId,
        version = this.version,
        cartData = cartData ?: this.cartData,
    )

    companion object {
        fun of(
            id: Long? = null,
            userId: Int,
            version: Long = 0L,
            cartData: String = "[]"
        ): Cart = Cart(
            id = id,
            userId = userId,
            version = version,
            cartData = cartData
        )
    }
}