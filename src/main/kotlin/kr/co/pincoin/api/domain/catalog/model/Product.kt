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
        name: String = this.name,
        subtitle: String = this.subtitle,
        code: String = this.code,
        listPrice: BigDecimal = this.listPrice,
        sellingPrice: BigDecimal = this.sellingPrice,
        pg: Boolean = this.pg,
        pgSellingPrice: BigDecimal = this.pgSellingPrice,
        status: ProductStatus = this.status,
        stockQuantity: Int = this.stockQuantity,
        stock: ProductStock = this.stock,
        minimumStockLevel: Int = this.minimumStockLevel,
        maximumStockLevel: Int = this.maximumStockLevel,
        reviewCount: Int = this.reviewCount,
        reviewCountPg: Int = this.reviewCountPg
    ): Product = Product(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = this.isRemoved,
        name = name,
        subtitle = subtitle,
        code = this.code,
        listPrice = listPrice,
        sellingPrice = sellingPrice,
        pg = pg,
        pgSellingPrice = pgSellingPrice,
        description = this.description,
        categoryId = this.categoryId,
        position = this.position,
        status = status,
        stockQuantity = stockQuantity,
        stock = stock,
        minimumStockLevel = minimumStockLevel,
        maximumStockLevel = maximumStockLevel,
        reviewCount = reviewCount,
        reviewCountPg = reviewCountPg,
        naverPartner = this.naverPartner,
        naverPartnerTitle = this.naverPartnerTitle,
        naverPartnerTitlePg = this.naverPartnerTitlePg,
        naverAttribute = this.naverAttribute
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