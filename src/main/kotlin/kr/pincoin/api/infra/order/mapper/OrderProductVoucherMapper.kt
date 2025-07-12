package kr.pincoin.api.infra.order.mapper

import kr.pincoin.api.domain.order.model.OrderProductVoucher
import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity

fun OrderProductVoucherEntity?.toModel(): OrderProductVoucher? =
    this?.let { entity ->
        OrderProductVoucher.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            code = entity.code,
            revoked = entity.revoked,
            remarks = entity.remarks,
            orderProductId = entity.orderProductId,
            voucherId = entity.voucherId,
        )
    }

fun List<OrderProductVoucherEntity>?.toModelList(): List<OrderProductVoucher> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderProductVoucher?.toEntity(): OrderProductVoucherEntity? =
    this?.let { model ->
        OrderProductVoucherEntity.of(
            id = model.id,
            code = model.code,
            revoked = model.revoked,
            remarks = model.remarks,
            orderProductId = model.orderProductId,
            voucherId = model.voucherId,
            isRemoved = model.isRemoved,
        )
    }