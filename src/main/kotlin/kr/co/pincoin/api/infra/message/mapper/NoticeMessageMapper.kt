package kr.co.pincoin.api.infra.message.mapper

import kr.co.pincoin.api.domain.message.model.NoticeMessage
import kr.co.pincoin.api.infra.message.entity.NoticeMessageEntity

fun NoticeMessageEntity?.toModel(): NoticeMessage? =
    this?.let { entity ->
        NoticeMessage.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            title = entity.title,
            description = entity.description,
            keywords = entity.keywords,
            content = entity.content,
            category = entity.category,
            ownerId = entity.ownerId,
        )
    }

fun List<NoticeMessageEntity>?.toModelList(): List<NoticeMessage> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun NoticeMessage?.toEntity(): NoticeMessageEntity? =
    this?.let { model ->
        NoticeMessageEntity.of(
            id = model.id,
            title = model.title,
            description = model.description,
            keywords = model.keywords,
            content = model.content,
            category = model.category,
            ownerId = model.ownerId,
        )
    }
