package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.SocialToken
import kr.co.pincoin.api.infra.oauth2.entity.SocialTokenEntity

fun SocialTokenEntity?.toModel(): SocialToken? =
    this?.let { entity ->
        SocialToken.of(
            id = entity.id,
            token = entity.token,
            tokenSecret = entity.tokenSecret,
            expiresAt = entity.expiresAt,
            accountId = entity.accountId,
            appId = entity.appId
        )
    }

fun List<SocialTokenEntity>?.toModelList(): List<SocialToken> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun SocialToken?.toEntity(): SocialTokenEntity? =
    this?.let { model ->
        SocialTokenEntity.of(
            id = model.id,
            token = model.token,
            tokenSecret = model.tokenSecret,
            expiresAt = model.expiresAt,
            accountId = model.accountId,
            appId = model.appId
        )
    }

fun List<SocialToken>?.toEntityList(): List<SocialTokenEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()