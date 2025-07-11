package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.PhoneBanned
import kr.pincoin.api.infra.user.entity.PhoneBannedEntity

fun PhoneBannedEntity?.toModel(): PhoneBanned? =
    this?.let { entity ->
        PhoneBanned.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            phone = entity.phone,
        )
    }

fun List<PhoneBannedEntity>?.toModelList(): List<PhoneBanned> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun PhoneBanned?.toEntity(): PhoneBannedEntity? =
    this?.let { model ->
        PhoneBannedEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            phone = model.phone,
        )
    }