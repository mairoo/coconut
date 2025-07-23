package kr.pincoin.api.infra.social.mapper

import kr.pincoin.api.domain.social.model.EmailConfirmation
import kr.pincoin.api.infra.social.entity.EmailConfirmationEntity

fun EmailConfirmationEntity?.toModel(): EmailConfirmation? =
    this?.let { entity ->
        EmailConfirmation.of(
            id = entity.id,
            created = entity.created,
            sent = entity.sent,
            key = entity.key,
            emailAddressId = entity.emailAddressId
        )
    }

fun List<EmailConfirmationEntity>?.toModelList(): List<EmailConfirmation> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun EmailConfirmation?.toEntity(): EmailConfirmationEntity? =
    this?.let { model ->
        EmailConfirmationEntity.of(
            id = model.id,
            created = model.created,
            sent = model.sent,
            key = model.key,
            emailAddressId = model.emailAddressId
        )
    }