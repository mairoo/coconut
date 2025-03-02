package kr.co.pincoin.api.infra.oauth2.mapper

import kr.co.pincoin.api.domain.oauth2.model.SocialAppSites
import kr.co.pincoin.api.infra.oauth2.entity.SocialAppSitesEntity

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
