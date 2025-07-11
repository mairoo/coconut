package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.PhoneVerificationLog
import kr.pincoin.api.infra.user.entity.PhoneVerificationLogEntity

fun PhoneVerificationLogEntity?.toModel(): PhoneVerificationLog? =
    this?.let { entity ->
        PhoneVerificationLog.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            token = entity.token,
            code = entity.code,
            reason = entity.reason,
            resultCode = entity.resultCode,
            message = entity.message,
            transactionId = entity.transactionId,
            di = entity.di,
            ci = entity.ci,
            fullname = entity.fullname,
            dateOfBirth = entity.dateOfBirth,
            gender = entity.gender,
            domestic = entity.domestic,
            telecom = entity.telecom,
            cellphone = entity.cellphone,
            returnMessage = entity.returnMessage,
            ownerId = entity.ownerId,
        )
    }

fun List<PhoneVerificationLogEntity>?.toModelList(): List<PhoneVerificationLog> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun PhoneVerificationLog?.toEntity(): PhoneVerificationLogEntity? =
    this?.let { model ->
        PhoneVerificationLogEntity.of(
            id = model.id,
            token = model.token,
            code = model.code,
            reason = model.reason,
            resultCode = model.resultCode,
            message = model.message,
            transactionId = model.transactionId,
            di = model.di,
            ci = model.ci,
            fullname = model.fullname,
            dateOfBirth = model.dateOfBirth,
            gender = model.gender,
            domestic = model.domestic,
            telecom = model.telecom,
            cellphone = model.cellphone,
            returnMessage = model.returnMessage,
            ownerId = model.ownerId
        )
    }