package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.EmailAddress
import kr.co.pincoin.api.infra.oauth2.entity.EmailAddressEntity

fun EmailAddressEntity?.toModel(): EmailAddress? =
    this?.let { entity ->
        EmailAddress.of(
            id = entity.id,
            email = entity.email,
            verified = entity.verified,
            primary = entity.primary,
            userId = entity.userId
        )
    }

fun List<EmailAddressEntity>?.toModelList(): List<EmailAddress> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun EmailAddress?.toEntity(): EmailAddressEntity? =
    this?.let { model ->
        EmailAddressEntity.of(
            id = model.id,
            email = model.email,
            verified = model.verified,
            primary = model.primary,
            userId = model.userId
        )
    }
