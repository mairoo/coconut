package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.TotpDevice
import kr.pincoin.api.infra.user.entity.TotpDeviceEntity

fun TotpDeviceEntity?.toModel(): TotpDevice? =
    this?.let { entity ->
        TotpDevice.of(
            id = entity.id,
            name = entity.name,
            confirmed = entity.confirmed,
            key = entity.key,
            step = entity.step,
            t0 = entity.t0,
            digits = entity.digits,
            tolerance = entity.tolerance,
            drift = entity.drift,
            lastT = entity.lastT,
            userId = entity.userId,
            throttlingFailureCount = entity.throttlingFailureCount,
            throttlingFailureTimestamp = entity.throttlingFailureTimestamp,
        )
    }

fun List<TotpDeviceEntity>?.toModelList(): List<TotpDevice> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun TotpDevice?.toEntity(): TotpDeviceEntity? =
    this?.let { model ->
        TotpDeviceEntity.of(
            id = model.id,
            name = model.name,
            confirmed = model.confirmed,
            key = model.key,
            step = model.step,
            t0 = model.t0,
            digits = model.digits,
            tolerance = model.tolerance,
            drift = model.drift,
            lastT = model.lastT,
            userId = model.userId,
            throttlingFailureCount = model.throttlingFailureCount,
            throttlingFailureTimestamp = model.throttlingFailureTimestamp,
        )
    }