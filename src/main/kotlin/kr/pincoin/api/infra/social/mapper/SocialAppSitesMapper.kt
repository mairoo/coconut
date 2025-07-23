package kr.pincoin.api.infra.social.mapper

import kr.pincoin.api.domain.social.model.SocialAppSites
import kr.pincoin.api.infra.social.entity.SocialAppSitesEntity

fun SocialAppSitesEntity?.toModel(): SocialAppSites? =
    this?.let { entity ->
        SocialAppSites.of(
            id = entity.id,
            socialAppId = entity.socialAppId,
            siteId = entity.siteId
        )
    }

fun List<SocialAppSitesEntity>?.toModelList(): List<SocialAppSites> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun SocialAppSites?.toEntity(): SocialAppSitesEntity? =
    this?.let { model ->
        SocialAppSitesEntity.of(
            id = model.id,
            socialAppId = model.socialAppId,
            siteId = model.siteId
        )
    }