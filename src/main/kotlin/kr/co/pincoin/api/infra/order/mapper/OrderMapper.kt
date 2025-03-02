package kr.co.pincoin.api.infra.order.mapper

import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.infra.order.entity.OrderEntity

fun OrderEntity?.toModel(): Order? =
    this?.let { entity ->
        Order.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderNo = entity.orderNo,
            userId = entity.userId,
            fullname = entity.fullname,
            userAgent = entity.userAgent,
            acceptLanguage = entity.acceptLanguage,
            ipAddress = entity.ipAddress,
            paymentMethod = entity.paymentMethod,
            transactionId = entity.transactionId,
            status = entity.status,
            visible = entity.visible,
            totalListPrice = entity.totalListPrice,
            totalSellingPrice = entity.totalSellingPrice,
            currency = entity.currency,
            message = entity.message,
            parentId = entity.parentId,
            suspicious = entity.suspicious
        )
    }

fun List<OrderEntity>?.toModelList(): List<Order> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Order?.toEntity(): OrderEntity? =
    this?.let { model ->
        OrderEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            orderNo = model.orderNo,
            userId = model.userId,
            fullname = model.fullname,
            userAgent = model.userAgent,
            acceptLanguage = model.acceptLanguage,
            ipAddress = model.ipAddress,
            paymentMethod = model.paymentMethod,
            transactionId = model.transactionId,
            status = model.status,
            visible = model.visible,
            totalListPrice = model.totalListPrice,
            totalSellingPrice = model.totalSellingPrice,
            currency = model.currency,
            message = model.message,
            parentId = model.parentId,
            suspicious = model.suspicious
        )
    }
