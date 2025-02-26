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
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val code: String,
    val storeId: Long,
    val categoryId: Long,

    // 4. 도메인 로직 가변 필드
    name: String,
    subtitle: String,
    listPrice: BigDecimal,
    sellingPrice: BigDecimal,
    pg: Boolean,
    pgSellingPrice: BigDecimal,
    description: String,
    position: Int,
    status: ProductStatus,
    stockQuantity: Int,
    stock: ProductStock,
    minimumStockLevel: Int,
    maximumStockLevel: Int,
    reviewCount: Int,
    reviewCountPg: Int,
    naverPartner: Boolean,
    naverPartnerTitle: String,
    naverPartnerTitlePg: String,
    naverAttribute: String,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var name: String = name
        private set

    var subtitle: String = subtitle
        private set

    var listPrice: BigDecimal = listPrice
        private set

    var sellingPrice: BigDecimal = sellingPrice
        private set

    var pg: Boolean = pg
        private set

    var pgSellingPrice: BigDecimal = pgSellingPrice
        private set

    var description: String = description
        private set

    var position: Int = position
        private set

    var status: ProductStatus = status
        private set

    var stockQuantity: Int = stockQuantity
        private set

    var stock: ProductStock = stock
        private set

    var minimumStockLevel: Int = minimumStockLevel
        private set

    var maximumStockLevel: Int = maximumStockLevel
        private set

    var reviewCount: Int = reviewCount
        private set

    var reviewCountPg: Int = reviewCountPg
        private set

    var naverPartner: Boolean = naverPartner
        private set

    var naverPartnerTitle: String = naverPartnerTitle
        private set

    var naverPartnerTitlePg: String = naverPartnerTitlePg
        private set

    var naverAttribute: String = naverAttribute
        private set

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
            storeId: Long,
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
                isRemoved = isRemoved,
                name = name,
                subtitle = subtitle,
                code = code,
                listPrice = listPrice,
                sellingPrice = sellingPrice,
                pg = pg,
                pgSellingPrice = pgSellingPrice,
                description = description,
                storeId = storeId,
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