package kr.pincoin.api.infra.order.mapper

import kr.pincoin.api.domain.order.model.OrderPayment
import kr.pincoin.api.infra.order.entity.OrderPaymentEntity

fun OrderPaymentEntity?.toModel(): OrderPayment? =
    this?.let { entity ->
        OrderPayment.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            account = entity.account,
            amount = entity.amount,
            received = entity.received,
            orderId = entity.orderId,
            balance = entity.balance,
        )
    }

fun List<OrderPaymentEntity>?.toModelList(): List<OrderPayment> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderPayment?.toEntity(): OrderPaymentEntity? =
    this?.let { model ->
        OrderPaymentEntity.of(
            id = model.id,
            account = model.account,
            amount = model.amount,
            received = model.received,
            orderId = model.orderId,
            balance = model.balance,
            isRemoved = model.isRemoved,
        )
    }