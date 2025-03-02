package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.SocialApp
import kr.co.pincoin.api.infra.oauth2.entity.SocialAppEntity

fun SocialAppEntity?.toModel(): SocialApp? =
    this?.let { entity ->
        SocialApp.of(
            id = entity.id,
            provider = entity.provider,
            name = entity.name,
            clientId = entity.clientId,
            secret = entity.secret,
            key = entity.key
        )
    }

fun List<SocialAppEntity>?.toModelList(): List<SocialApp> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun SocialApp?.toEntity(): SocialAppEntity? =
    this?.let { model ->
        SocialAppEntity.of(
            id = model.id,
            provider = model.provider,
            name = model.name,
            clientId = model.clientId,
            secret = model.secret,
            key = model.key
        )
    }
