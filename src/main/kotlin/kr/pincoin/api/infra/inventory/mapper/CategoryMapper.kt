package kr.pincoin.api.infra.inventory.mapper

import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.infra.inventory.entity.CategoryEntity

fun CategoryEntity?.toModel(): Category? =
    this?.let { entity ->
        Category.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            title = entity.title,
            slug = entity.slug,
            thumbnail = entity.thumbnail,
            description = entity.description,
            description1 = entity.description1,
            lft = entity.lft,
            rght = entity.rght,
            treeId = entity.treeId,
            level = entity.level,
            parentId = entity.parentId,
            storeId = entity.storeId,
            discountRate = entity.discountRate,
            pg = entity.pg,
            pgDiscountRate = entity.pgDiscountRate,
            naverSearchTag = entity.naverSearchTag,
            naverBrandName = entity.naverBrandName,
            naverMakerName = entity.naverMakerName,
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
            thumbnail = model.thumbnail,
            description = model.description,
            description1 = model.description1,
            lft = model.lft,
            rght = model.rght,
            treeId = model.treeId,
            level = model.level,
            parentId = model.parentId,
            storeId = model.storeId,
            discountRate = model.discountRate,
            pg = model.pg,
            pgDiscountRate = model.pgDiscountRate,
            naverSearchTag = model.naverSearchTag,
            naverBrandName = model.naverBrandName,
            naverMakerName = model.naverMakerName,
        )
    }