package kr.co.pincoin.api.infra.inquiry.mapper

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestion
import kr.co.pincoin.api.infra.inquiry.entity.CustomerQuestionEntity

fun CustomerQuestionEntity?.toModel(): CustomerQuestion? =
    this?.let { entity ->
        CustomerQuestion.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            title = entity.title,
            description = entity.description,
            keywords = entity.keywords,
            content = entity.content,
            category = entity.category,
            orderId = entity.orderId,
            ownerId = entity.ownerId,
        )
    }

fun List<CustomerQuestionEntity>?.toModelList(): List<CustomerQuestion> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun CustomerQuestion?.toEntity(): CustomerQuestionEntity? =
    this?.let { model ->
        CustomerQuestionEntity.of(
            id = model.id,
            title = model.title,
            description = model.description,
            keywords = model.keywords,
            content = model.content,
            category = model.category,
            orderId = model.orderId,
            ownerId = model.ownerId,
        )
    }
