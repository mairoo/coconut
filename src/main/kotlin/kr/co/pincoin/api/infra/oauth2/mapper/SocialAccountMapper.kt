package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.SocialAccount
import kr.co.pincoin.api.infra.oauth2.entity.SocialAccountEntity

fun SocialAccountEntity?.toModel(): SocialAccount? =
    this?.let { entity ->
        SocialAccount.of(
            id = entity.id,
            provider = entity.provider,
            uid = entity.uid,
            lastLogin = entity.lastLogin,
            dateJoined = entity.dateJoined,
            extraData = entity.extraData,
            userId = entity.userId
        )
    }

fun List<SocialAccountEntity>?.toModelList(): List<SocialAccount> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun SocialAccount?.toEntity(): SocialAccountEntity? =
    this?.let { model ->
        SocialAccountEntity.of(
            id = model.id,
            provider = model.provider,
            uid = model.uid,
            lastLogin = model.lastLogin,
            dateJoined = model.dateJoined,
            extraData = model.extraData,
            userId = model.userId
        )
    }
