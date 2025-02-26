package kr.co.pincoin.api.infra.order.mapper

import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.infra.order.entity.OrderPaymentEntity

fun OrderPaymentEntity?.toModel(): OrderPayment? =
    this?.let { entity ->
        OrderPayment.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderId = entity.orderId,
            account = entity.account,
            amount = entity.amount,
            balance = entity.balance,
            received = entity.received
        )
    }

fun List<OrderPaymentEntity>?.toModelList(): List<OrderPayment> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderPayment?.toEntity(): OrderPaymentEntity? =
    this?.let { model ->
        OrderPaymentEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            orderId = model.orderId,
            account = model.account,
            amount = model.amount,
            balance = model.balance,
            received = model.received
        )
    }

fun List<OrderPayment>?.toEntityList(): List<OrderPaymentEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()