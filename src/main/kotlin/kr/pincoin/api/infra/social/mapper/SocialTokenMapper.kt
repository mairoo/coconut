package kr.pincoin.api.infra.social.mapper

import kr.pincoin.api.domain.social.model.SocialToken
import kr.pincoin.api.infra.social.entity.SocialTokenEntity

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