package kr.co.pincoin.api.infra.review.mapper

import kr.co.pincoin.api.domain.review.model.Testimonial
import kr.co.pincoin.api.infra.review.entity.TestimonialEntity

fun TestimonialEntity?.toModel(): Testimonial? =
    this?.let { entity ->
        Testimonial.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            title = entity.title,
            description = entity.description,
            keywords = entity.keywords,
            content = entity.content,
            ownerId = entity.ownerId,
        )
    }

fun List<TestimonialEntity>?.toModelList(): List<Testimonial> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Testimonial?.toEntity(): TestimonialEntity? =
    this?.let { model ->
        TestimonialEntity.of(
            id = model.id,
            title = model.title,
            description = model.description,
            keywords = model.keywords,
            content = model.content,
            ownerId = model.ownerId,
        )
    }
