package kr.co.pincoin.api.infra.catalog.mapper

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.infra.catalog.entity.ProductEntity

fun ProductEntity?.toModel(): Product? =
    this?.let { entity ->
        Product.of(
            id = entity.id,
            created = entity.dateTimeFields.created,
            modified = entity.dateTimeFields.modified,
            isRemoved = entity.removalFields.isRemoved,
            name = entity.name,
            subtitle = entity.subtitle,
            code = entity.code,
            listPrice = entity.listPrice,
            sellingPrice = entity.sellingPrice,
            pg = entity.pg,
            pgSellingPrice = entity.pgSellingPrice,
            description = entity.description,
            categoryId = entity.categoryId,
            position = entity.position,
            status = entity.status,
            stockQuantity = entity.stockQuantity,
            stock = entity.stock,
            minimumStockLevel = entity.minimumStockLevel,
            maximumStockLevel = entity.maximumStockLevel,
            reviewCount = entity.reviewCount,
            reviewCountPg = entity.reviewCountPg,
            naverPartner = entity.naverPartner,
            naverPartnerTitle = entity.naverPartnerTitle,
            naverPartnerTitlePg = entity.naverPartnerTitlePg,
            naverAttribute = entity.naverAttribute
        )
    }

fun List<ProductEntity>?.toModelList(): List<Product> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Product?.toEntity(): ProductEntity? =
    this?.let { model ->
        ProductEntity.of(
            id = model.id,
            isRemoved = model.isRemoved,
            name = model.name,
            subtitle = model.subtitle,
            code = model.code,
            listPrice = model.listPrice,
            sellingPrice = model.sellingPrice,
            pg = model.pg,
            pgSellingPrice = model.pgSellingPrice,
            description = model.description,
            categoryId = model.categoryId,
            position = model.position,
            status = model.status,
            stockQuantity = model.stockQuantity,
            stock = model.stock,
            minimumStockLevel = model.minimumStockLevel,
            maximumStockLevel = model.maximumStockLevel,
            reviewCount = model.reviewCount,
            reviewCountPg = model.reviewCountPg,
            naverPartner = model.naverPartner,
            naverPartnerTitle = model.naverPartnerTitle,
            naverPartnerTitlePg = model.naverPartnerTitlePg,
            naverAttribute = model.naverAttribute
        )
    }

fun List<Product>?.toEntityList(): List<ProductEntity> =
    this?.mapNotNull { it.toEntity() } ?: emptyList()