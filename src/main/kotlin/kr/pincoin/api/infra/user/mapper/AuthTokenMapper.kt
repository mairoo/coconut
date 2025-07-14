package kr.pincoin.api.infra.user.mapper

import kr.pincoin.api.domain.user.model.AuthToken
import kr.pincoin.api.infra.user.entity.AuthTokenEntity

fun AuthTokenEntity?.toModel(): AuthToken? =
    this?.let { entity ->
        AuthToken.of(
            key = entity.key,
            userId = entity.userId,
            created = entity.created,
        )
    }

fun List<AuthTokenEntity>?.toModelList(): List<AuthToken> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun AuthToken?.toEntity(): AuthTokenEntity? =
    this?.let { model ->
        AuthTokenEntity.of(
            key = model.key,
            userId = model.userId,
            created = model.created ?: java.time.LocalDateTime.now(),
        )
    }