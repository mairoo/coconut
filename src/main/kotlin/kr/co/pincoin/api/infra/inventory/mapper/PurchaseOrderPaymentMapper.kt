package kr.co.pincoin.api.infra.inventory.mapper

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrderPayment
import kr.co.pincoin.api.infra.inventory.entity.PurchaseOrderPaymentEntity

fun PurchaseOrderPaymentEntity?.toModel(): PurchaseOrderPayment? =
    this?.let { entity ->
        PurchaseOrderPayment.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderId = entity.orderId,
            account = entity.account,
            amount = entity.amount
        )
    }

fun List<PurchaseOrderPaymentEntity>?.toModelList(): List<PurchaseOrderPayment> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun PurchaseOrderPayment?.toEntity(): PurchaseOrderPaymentEntity? =
    this?.let { model ->
        PurchaseOrderPaymentEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            orderId = model.orderId,
            account = model.account,
            amount = model.amount
        )
    }

fun List<PurchaseOrderPayment>?.toEntityList(): List<PurchaseOrderPaymentEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()