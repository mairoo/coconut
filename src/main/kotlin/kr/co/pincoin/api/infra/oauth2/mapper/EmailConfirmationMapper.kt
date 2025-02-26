package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.EmailConfirmation
import kr.co.pincoin.api.infra.oauth2.entity.EmailConfirmationEntity

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

fun List<EmailConfirmation>?.toEntityList(): List<EmailConfirmationEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()