package kr.pincoin.api.infra.inventory.mapper

import kr.pincoin.api.domain.inventory.model.PurchaseOrder
import kr.pincoin.api.infra.inventory.entity.PurchaseOrderEntity

fun PurchaseOrderEntity?.toModel(): PurchaseOrder? =
    this?.let { entity ->
        PurchaseOrder.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            title = entity.title,
            content = entity.content,
            bankAccount = entity.bankAccount,
            amount = entity.amount,
            paid = entity.paid,
        )
    }

fun List<PurchaseOrderEntity>?.toModelList(): List<PurchaseOrder> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun PurchaseOrder?.toEntity(): PurchaseOrderEntity? =
    this?.let { model ->
        PurchaseOrderEntity.of(
            id = model.id,
            title = model.title,
            content = model.content,
            bankAccount = model.bankAccount,
            amount = model.amount,
            paid = model.paid,
            isRemoved = model.isRemoved,
        )
    }