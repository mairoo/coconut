package kr.co.pincoin.api.infra.user.mapper

import kr.co.pincoin.api.domain.user.model.LoginLog
import kr.co.pincoin.api.infra.user.entity.LoginLogEntity

fun LoginLogEntity?.toModel(): LoginLog? =
    this?.let { entity ->
        LoginLog.of(
            id = entity.id,
            created = entity.created,
            modified = entity.modified,
            ipAddress = entity.ipAddress,
            email = entity.email,
            username = entity.username,
            userAgent = entity.userAgent,
            isSuccessful = entity.isSuccessful,
            reason = entity.reason,
            userId = entity.userId
        )
    }

fun List<LoginLogEntity>?.toModelList(): List<LoginLog> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun LoginLog?.toEntity(): LoginLogEntity? =
    this?.let { model ->
        LoginLogEntity.of(
            id = model.id,
            created = model.created,
            modified = model.modified,
            ipAddress = model.ipAddress,
            email = model.email,
            username = model.username,
            userAgent = model.userAgent,
            isSuccessful = model.isSuccessful,
            reason = model.reason,
            userId = model.userId
        )
    }
