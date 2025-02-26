package kr.co.pincoin.api.infra.order.mapper

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.infra.order.entity.OrderProductVoucherEntity

fun OrderProductVoucherEntity?.toModel(): OrderProductVoucher? =
    this?.let { entity ->
        OrderProductVoucher.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            orderProductId = entity.orderProductId,
            voucherId = entity.voucherId,
            code = entity.code,
            revoked = entity.revoked,
            remarks = entity.remarks
        )
    }

fun List<OrderProductVoucherEntity>?.toModelList(): List<OrderProductVoucher> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderProductVoucher?.toEntity(): OrderProductVoucherEntity? =
    this?.let { model ->
        OrderProductVoucherEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            orderProductId = model.orderProductId,
            voucherId = model.voucherId,
            code = model.code,
            revoked = model.revoked,
            remarks = model.remarks
        )
    }

fun List<OrderProductVoucher>?.toEntityList(): List<OrderProductVoucherEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()