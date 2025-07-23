package kr.pincoin.api.infra.support.mapper

import kr.pincoin.api.domain.support.model.TestimonialAnswer
import kr.pincoin.api.infra.support.entity.TestimonialAnswerEntity

fun TestimonialAnswerEntity?.toModel(): TestimonialAnswer? =
    this?.let { entity ->
        TestimonialAnswer.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            content = entity.content,
            testimonialId = entity.testimonialId,
        )
    }

fun List<TestimonialAnswerEntity>?.toModelList(): List<TestimonialAnswer> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun TestimonialAnswer?.toEntity(): TestimonialAnswerEntity? =
    this?.let { model ->
        TestimonialAnswerEntity.of(
            id = model.id,
            content = model.content,
            testimonialId = model.testimonialId,
        )
    }