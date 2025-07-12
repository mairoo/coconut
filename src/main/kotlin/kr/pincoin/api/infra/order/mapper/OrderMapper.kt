package kr.pincoin.api.infra.order.mapper

import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.infra.order.entity.OrderEntity

fun OrderEntity?.toModel(): Order? =
    this?.let { entity ->
        Order.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderNo = entity.orderNo,
            userAgent = entity.userAgent,
            acceptLanguage = entity.acceptLanguage,
            ipAddress = entity.ipAddress,
            paymentMethod = entity.paymentMethod,
            status = entity.status,
            totalListPrice = entity.totalListPrice,
            totalSellingPrice = entity.totalSellingPrice,
            currency = entity.currency,
            message = entity.message,
            parentId = entity.parentId,
            userId = entity.userId,
            fullname = entity.fullname,
            transactionId = entity.transactionId,
            visible = entity.visible,
            suspicious = entity.suspicious,
        )
    }

fun List<OrderEntity>?.toModelList(): List<Order> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Order?.toEntity(): OrderEntity? =
    this?.let { model ->
        OrderEntity.of(
            id = model.id,
            orderNo = model.orderNo,
            userAgent = model.userAgent,
            acceptLanguage = model.acceptLanguage,
            ipAddress = model.ipAddress,
            paymentMethod = model.paymentMethod,
            status = model.status,
            totalListPrice = model.totalListPrice,
            totalSellingPrice = model.totalSellingPrice,
            currency = model.currency,
            message = model.message,
            parentId = model.parentId,
            userId = model.userId,
            fullname = model.fullname,
            transactionId = model.transactionId,
            visible = model.visible,
            suspicious = model.suspicious,
            isRemoved = model.isRemoved,
        )
    }