package kr.pincoin.api.infra.support.mapper

import kr.pincoin.api.domain.support.model.CustomerQuestionAnswer
import kr.pincoin.api.infra.support.entity.CustomerQuestionAnswerEntity

fun CustomerQuestionAnswerEntity?.toModel(): CustomerQuestionAnswer? =
    this?.let { entity ->
        CustomerQuestionAnswer.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            content = entity.content,
            questionId = entity.questionId,
        )
    }

fun List<CustomerQuestionAnswerEntity>?.toModelList(): List<CustomerQuestionAnswer> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun CustomerQuestionAnswer?.toEntity(): CustomerQuestionAnswerEntity? =
    this?.let { model ->
        CustomerQuestionAnswerEntity.of(
            id = model.id,
            content = model.content,
            questionId = model.questionId,
        )
    }