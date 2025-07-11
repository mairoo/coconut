package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.LoginLog
import kr.pincoin.api.infra.user.entity.LoginLogEntity

fun LoginLogEntity?.toModel(): LoginLog? =
    this?.let { entity ->
        LoginLog.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            ipAddress = entity.ipAddress,
            userId = entity.userId,
        )
    }

fun List<LoginLogEntity>?.toModelList(): List<LoginLog> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun LoginLog?.toEntity(): LoginLogEntity? =
    this?.let { model ->
        LoginLogEntity.of(
            id = model.id,
            ipAddress = model.ipAddress,
            userId = model.userId
        )
    }