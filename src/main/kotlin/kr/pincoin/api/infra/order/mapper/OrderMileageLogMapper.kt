package kr.pincoin.api.infra.order.mapper

import kr.pincoin.api.domain.order.model.OrderMileageLog
import kr.pincoin.api.infra.order.entity.OrderMileageLogEntity

fun OrderMileageLogEntity?.toModel(): OrderMileageLog? =
    this?.let { entity ->
        OrderMileageLog.of(
            id = entity.id,
            created = entity.created,
            modified = entity.modified,
            isRemoved = entity.isRemoved,
            mileage = entity.mileage,
            orderId = entity.orderId,
            userId = entity.userId,
            memo = entity.memo,
        )
    }

fun List<OrderMileageLogEntity>?.toModelList(): List<OrderMileageLog> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun OrderMileageLog?.toEntity(): OrderMileageLogEntity? =
    this?.let { model ->
        OrderMileageLogEntity.of(
            id = model.id,
            created = model.created ?: java.time.LocalDateTime.now(),
            modified = model.modified ?: java.time.LocalDateTime.now(),
            isRemoved = model.isRemoved,
            mileage = model.mileage,
            orderId = model.orderId,
            userId = model.userId,
            memo = model.memo,
        )
    }