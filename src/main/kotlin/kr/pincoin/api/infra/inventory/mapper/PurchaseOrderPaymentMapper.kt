package kr.pincoin.api.infra.inventory.mapper

import kr.pincoin.api.domain.inventory.model.PurchaseOrderPayment
import kr.pincoin.api.infra.inventory.entity.PurchaseOrderPaymentEntity

fun PurchaseOrderPaymentEntity?.toModel(): PurchaseOrderPayment? =
    this?.let { entity ->
        PurchaseOrderPayment.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            account = entity.account,
            amount = entity.amount,
            orderId = entity.orderId,
        )
    }

fun List<PurchaseOrderPaymentEntity>?.toModelList(): List<PurchaseOrderPayment> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun PurchaseOrderPayment?.toEntity(): PurchaseOrderPaymentEntity? =
    this?.let { model ->
        PurchaseOrderPaymentEntity.of(
            id = model.id,
            account = model.account,
            amount = model.amount,
            orderId = model.orderId,
            isRemoved = model.isRemoved,
        )
    }