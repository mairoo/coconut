package kr.co.pincoin.api.infra.review.mapper

import kr.co.pincoin.api.domain.review.model.TestimonialAnswer
import kr.co.pincoin.api.infra.review.entity.TestimonialAnswerEntity

fun TestimonialAnswerEntity?.toModel(): TestimonialAnswer? =
    this?.let { entity ->
        TestimonialAnswer.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            content = entity.content,
            testimonialId = entity.testimonialId
        )
    }

fun List<TestimonialAnswerEntity>?.toModelList(): List<TestimonialAnswer> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun TestimonialAnswer?.toEntity(): TestimonialAnswerEntity? =
    this?.let { model ->
        TestimonialAnswerEntity.of(
            id = model.id,
            content = model.content,
            testimonialId = model.testimonialId
        )
    }
