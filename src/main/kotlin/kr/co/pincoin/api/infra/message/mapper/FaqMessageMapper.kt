package kr.co.pincoin.api.infra.message.mapper

import kr.co.pincoin.api.domain.message.model.FaqMessage
import kr.co.pincoin.api.infra.message.entity.FaqMessageEntity

fun FaqMessageEntity?.toModel(): FaqMessage? =
    this?.let { entity ->
        FaqMessage.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            title = entity.title,
            description = entity.description,
            keywords = entity.keywords,
            content = entity.content,
            category = entity.category,
            position = entity.position,
            ownerId = entity.ownerId,
        )
    }

fun List<FaqMessageEntity>?.toModelList(): List<FaqMessage> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun FaqMessage?.toEntity(): FaqMessageEntity? =
    this?.let { model ->
        FaqMessageEntity.of(
            id = model.id,
            title = model.title,
            description = model.description,
            keywords = model.keywords,
            content = model.content,
            category = model.category,
            position = model.position,
            ownerId = model.ownerId,
        )
    }
