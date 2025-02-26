package kr.co.pincoin.api.infra.inquiry.mapper

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestionAnswer
import kr.co.pincoin.api.infra.inquiry.entity.CustomerQuestionAnswerEntity

fun CustomerQuestionAnswerEntity?.toModel(): CustomerQuestionAnswer? =
    this?.let { entity ->
        CustomerQuestionAnswer.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            content = entity.content,
            questionId = entity.questionId
        )
    }

fun List<CustomerQuestionAnswerEntity>?.toModelList(): List<CustomerQuestionAnswer> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun CustomerQuestionAnswer?.toEntity(): CustomerQuestionAnswerEntity? =
    this?.let { model ->
        CustomerQuestionAnswerEntity.of(
            id = model.id,
            content = model.content,
            questionId = model.questionId
        )
    }

fun List<CustomerQuestionAnswer>?.toEntityList(): List<CustomerQuestionAnswerEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()