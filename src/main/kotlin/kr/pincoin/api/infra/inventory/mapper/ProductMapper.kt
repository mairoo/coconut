package kr.pincoin.api.infra.inventory.mapper

import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.infra.inventory.entity.ProductEntity

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
            description = entity.description,
            position = entity.position,
            status = entity.status,
            stock = entity.stock,
            categoryId = entity.categoryId,
            storeId = entity.storeId,
            reviewCount = entity.reviewCount,
            naverPartner = entity.naverPartner,
            naverPartnerTitle = entity.naverPartnerTitle,
            minimumStockLevel = entity.minimumStockLevel,
            pg = entity.pg,
            pgSellingPrice = entity.pgSellingPrice,
            naverAttribute = entity.naverAttribute,
            naverPartnerTitlePg = entity.naverPartnerTitlePg,
            reviewCountPg = entity.reviewCountPg,
            maximumStockLevel = entity.maximumStockLevel,
            stockQuantity = entity.stockQuantity,
        )
    }

fun List<ProductEntity>?.toModelList(): List<Product> =
    this?.mapNotNull { it.toModel() } ?: emptyList()

fun Product?.toEntity(): ProductEntity? =
    this?.let { model ->
        ProductEntity.of(
            id = model.id,
            name = model.name,
            subtitle = model.subtitle,
            code = model.code,
            listPrice = model.listPrice,
            sellingPrice = model.sellingPrice,
            description = model.description,
            position = model.position,
            status = model.status,
            stock = model.stock,
            categoryId = model.categoryId,
            storeId = model.storeId,
            reviewCount = model.reviewCount,
            naverPartner = model.naverPartner,
            naverPartnerTitle = model.naverPartnerTitle,
            minimumStockLevel = model.minimumStockLevel,
            pg = model.pg,
            pgSellingPrice = model.pgSellingPrice,
            naverAttribute = model.naverAttribute,
            naverPartnerTitlePg = model.naverPartnerTitlePg,
            reviewCountPg = model.reviewCountPg,
            maximumStockLevel = model.maximumStockLevel,
            stockQuantity = model.stockQuantity,
            isRemoved = model.isRemoved,
        )
    }