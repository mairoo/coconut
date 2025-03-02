package kr.co.pincoin.api.infra.order.mapper

import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.infra.order.entity.OrderProductEntity

fun OrderProductEntity?.toModel(): OrderProduct? =
    this?.let { entity ->
        OrderProduct.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderId = entity.orderId,
            name = entity.name,
            subtitle = entity.subtitle,
            code = entity.code,
            listPrice = entity.listPrice,
            sellingPrice = entity.sellingPrice,
            quantity = entity.quantity
        )
    }

fun List<OrderProductEntity>?.toModelList(): List<OrderProduct> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderProduct?.toEntity(): OrderProductEntity? =
    this?.let { model ->
        OrderProductEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            orderId = model.orderId,
            name = model.name,
            subtitle = model.subtitle,
            code = model.code,
            listPrice = model.listPrice,
            sellingPrice = model.sellingPrice,
            quantity = model.quantity
        )
    }
