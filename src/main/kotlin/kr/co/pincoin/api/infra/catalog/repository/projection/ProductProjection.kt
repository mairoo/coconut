package kr.co.pincoin.api.infra.catalog.repository.projection

import com.querydsl.core.annotations.QueryProjection
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import java.math.BigDecimal
import java.time.ZonedDateTime

data class ProductProjection @QueryProjection constructor(
    // Product 정보
    val id: Long?,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,
    val isRemoved: Boolean,
    val name: String,
    val subtitle: String,
    val code: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val pg: Boolean,
    val pgSellingPrice: BigDecimal,
    val description: String,
    val storeId: Long,
    val categoryId: Long,
    val position: Int,
    val status: ProductStatus,
    val stockQuantity: Int,
    val stock: ProductStock,

    // Category 정보
    val categoryTitle: String,
    val categorySlug: String,
    val categoryThumbnail: String,
    val categoryDescription: String,
    val categoryDiscountRate: BigDecimal
)