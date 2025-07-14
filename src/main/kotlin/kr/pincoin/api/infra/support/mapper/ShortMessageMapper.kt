package kr.pincoin.api.infra.support.mapper

import kr.pincoin.api.domain.support.model.ShortMessage
import kr.pincoin.api.infra.support.entity.ShortMessageEntity

fun ShortMessageEntity?.toModel(): ShortMessage? =
    this?.let { entity ->
        ShortMessage.of(
            id = entity.id,
            created = entity.created,
            modified = entity.modified,
            phoneFrom = entity.phoneFrom,
            phoneTo = entity.phoneTo,
            content = entity.content,
            success = entity.success,
        )
    }

fun List<ShortMessageEntity>?.toModelList(): List<ShortMessage> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun ShortMessage?.toEntity(): ShortMessageEntity? =
    this?.let { model ->
        ShortMessageEntity.of(
            id = model.id,
            created = model.created ?: java.time.LocalDateTime.now(),
            modified = model.modified ?: java.time.LocalDateTime.now(),
            phoneFrom = model.phoneFrom,
            phoneTo = model.phoneTo,
            content = model.content,
            success = model.success,
        )
    }