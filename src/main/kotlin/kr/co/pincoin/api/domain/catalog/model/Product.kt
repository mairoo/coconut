package kr.co.pincoin.api.domain.catalog.model

import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import java.math.BigDecimal
import java.time.ZonedDateTime

class Product private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val categoryId: Long,

    // 4. 도메인 로직 가변 필드
    val name: String,
    val subtitle: String,
    val code: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val pg: Boolean,
    val pgSellingPrice: BigDecimal,
    val description: String,
    val position: Int,
    val status: ProductStatus,
    val stockQuantity: Int,
    val stock: ProductStock,
    val minimumStockLevel: Int,
    val maximumStockLevel: Int,
    val reviewCount: Int,
    val reviewCountPg: Int,
    val naverPartner: Boolean,
    val naverPartnerTitle: String,
    val naverPartnerTitlePg: String,
    val naverAttribute: String,
) {
    fun updateBasicInfo(
        newName: String? = null,
        newSubtitle: String? = null,
        newCode: String? = null,
    ): Product = copy(
        name = newName ?: name,
        subtitle = newSubtitle ?: subtitle,
        code = newCode ?: code,
    )

    fun updatePriceInfo(
        newListPrice: BigDecimal? = null,
        newSellingPrice: BigDecimal? = null,
        newPgSellingPrice: BigDecimal? = null
    ): Product = copy(
        listPrice = newListPrice ?: listPrice,
        sellingPrice = newSellingPrice ?: sellingPrice,
        pgSellingPrice = newPgSellingPrice ?: pgSellingPrice
    )

    fun updatePgStatus(newPg: Boolean? = null): Product = copy(
        pg = newPg ?: pg
    )

    fun updateStatus(newStatus: ProductStatus? = null): Product = copy(
        status = newStatus ?: status
    )

    fun updateStockStatus(newStock: ProductStock? = null): Product = copy(
        stock = newStock ?: stock
    )

    fun updateStockQuantity(newStockQuantity: Int? = null): Product = copy(
        stockQuantity = newStockQuantity ?: stockQuantity
    )

    fun updateStockLevels(
        newMinimumStockLevel: Int? = null,
        newMaximumStockLevel: Int? = null
    ): Product = copy(
        minimumStockLevel = newMinimumStockLevel ?: minimumStockLevel,
        maximumStockLevel = newMaximumStockLevel ?: maximumStockLevel
    )

    fun increaseReviewCount(): Product = copy(reviewCount = reviewCount + 1)

    fun increaseReviewCountPg(): Product = copy(reviewCountPg = reviewCountPg + 1)

    private fun copy(
        name: String? = null,
        subtitle: String? = null,
        code: String? = null,
        listPrice: BigDecimal? = null,
        sellingPrice: BigDecimal? = null,
        pg: Boolean? = null,
        pgSellingPrice: BigDecimal? = null,
        description: String? = null,
        position: Int? = null,
        status: ProductStatus? = null,
        stockQuantity: Int? = null,
        stock: ProductStock? = null,
        minimumStockLevel: Int? = null,
        maximumStockLevel: Int? = null,
        reviewCount: Int? = null,
        reviewCountPg: Int? = null,
        naverPartner: Boolean? = null,
        naverPartnerTitle: String? = null,
        naverPartnerTitlePg: String? = null,
        naverAttribute: String? = null
    ): Product = Product(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = this.isRemoved,
        categoryId = this.categoryId,
        name = name ?: this.name,
        subtitle = subtitle ?: this.subtitle,
        code = code ?: this.code,
        listPrice = listPrice ?: this.listPrice,
        sellingPrice = sellingPrice ?: this.sellingPrice,
        pg = pg ?: this.pg,
        pgSellingPrice = pgSellingPrice ?: this.pgSellingPrice,
        description = description ?: this.description,
        position = position ?: this.position,
        status = status ?: this.status,
        stockQuantity = stockQuantity ?: this.stockQuantity,
        stock = stock ?: this.stock,
        minimumStockLevel = minimumStockLevel ?: this.minimumStockLevel,
        maximumStockLevel = maximumStockLevel ?: this.maximumStockLevel,
        reviewCount = reviewCount ?: this.reviewCount,
        reviewCountPg = reviewCountPg ?: this.reviewCountPg,
        naverPartner = naverPartner ?: this.naverPartner,
        naverPartnerTitle = naverPartnerTitle ?: this.naverPartnerTitle,
        naverPartnerTitlePg = naverPartnerTitlePg ?: this.naverPartnerTitlePg,
        naverAttribute = naverAttribute ?: this.naverAttribute
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal,
            sellingPrice: BigDecimal,
            pg: Boolean = false,
            pgSellingPrice: BigDecimal = BigDecimal.ZERO,
            description: String = "",
            categoryId: Long,
            position: Int,
            status: ProductStatus = ProductStatus.ENABLED,
            stockQuantity: Int = 0,
            stock: ProductStock = ProductStock.IN_STOCK,
            minimumStockLevel: Int = 0,
            maximumStockLevel: Int = 0,
            reviewCount: Int = 0,
            reviewCountPg: Int = 0,
            naverPartner: Boolean = false,
            naverPartnerTitle: String = "",
            naverPartnerTitlePg: String = "",
            naverAttribute: String = "",
        ): Product =
            Product(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                name = name,
                subtitle = subtitle,
                code = code,
                listPrice = listPrice,
                sellingPrice = sellingPrice,
                pg = pg,
                pgSellingPrice = pgSellingPrice,
                description = description,
                categoryId = categoryId,
                position = position,
                status = status,
                stockQuantity = stockQuantity,
                stock = stock,
                minimumStockLevel = minimumStockLevel,
                maximumStockLevel = maximumStockLevel,
                reviewCount = reviewCount,
                reviewCountPg = reviewCountPg,
                naverPartner = naverPartner,
                naverPartnerTitle = naverPartnerTitle,
                naverPartnerTitlePg = naverPartnerTitlePg,
                naverAttribute = naverAttribute,
            )
    }
}