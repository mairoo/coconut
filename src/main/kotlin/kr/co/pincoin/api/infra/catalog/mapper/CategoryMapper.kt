package kr.co.pincoin.api.infra.catalog.mapper

import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.infra.catalog.entity.CategoryEntity

fun CategoryEntity?.toModel(): Category? =
    this?.let { entity ->
        Category.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            title = entity.title,
            slug = entity.slug,
            storeId = entity.storeId,
            thumbnail = entity.thumbnail,
            description = entity.description,
            description1 = entity.description1,
            discountRate = entity.discountRate,
            pg = entity.pg,
            pgDiscountRate = entity.pgDiscountRate,
            naverSearchTag = entity.naverSearchTag,
            naverBrandName = entity.naverBrandName,
            naverMakerName = entity.naverMakerName
        )
    }

fun List<CategoryEntity>?.toModelList(): List<Category> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Category?.toEntity(): CategoryEntity? =
    this?.let { model ->
        CategoryEntity.of(
            id = model.id,
            title = model.title,
            slug = model.slug,
            storeId = model.storeId,
            thumbnail = model.thumbnail,
            description = model.description,
            description1 = model.description1,
            discountRate = model.discountRate,
            pg = model.pg,
            pgDiscountRate = model.pgDiscountRate,
            naverSearchTag = model.naverSearchTag,
            naverBrandName = model.naverBrandName,
            naverMakerName = model.naverMakerName
            // created, modified: 매핑 안 함 JPA Auditing 관리 필드
        )
    }

fun List<Category>?.toEntityList(): List<CategoryEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()