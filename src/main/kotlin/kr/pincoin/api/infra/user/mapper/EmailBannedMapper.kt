package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.EmailBanned
import kr.pincoin.api.infra.user.entity.EmailBannedEntity

fun EmailBannedEntity?.toModel(): EmailBanned? =
    this?.let { entity ->
        EmailBanned.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            email = entity.email,
        )
    }

fun List<EmailBannedEntity>?.toModelList(): List<EmailBanned> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun EmailBanned?.toEntity(): EmailBannedEntity? =
    this?.let { model ->
        EmailBannedEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            email = model.email,
        )
    }