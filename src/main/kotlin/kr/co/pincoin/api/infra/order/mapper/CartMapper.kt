package kr.co.pincoin.api.infra.order.mapper

import kr.co.pincoin.api.domain.order.model.Cart
import kr.co.pincoin.api.infra.order.entity.CartEntity

fun CartEntity?.toModel(): Cart? =
    this?.let { entity ->
        Cart.of(
            id = entity.id,
            userId = entity.userId,
            version = entity.version,
            cartData = entity.cartData,
        )
    }

fun List<CartEntity>?.toModelList(): List<Cart> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Cart?.toEntity(): CartEntity? =
    this?.let { model ->
        CartEntity.of(
            id = model.id,
            userId = model.userId,
            version = model.version,
            cartData = model.cartData,
        )
    }
