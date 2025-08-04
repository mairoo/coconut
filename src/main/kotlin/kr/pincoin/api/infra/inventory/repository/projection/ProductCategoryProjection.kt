package kr.pincoin.api.infra.inventory.repository.projection

import com.querydsl.core.annotations.QueryProjection
import java.math.BigDecimal
import java.time.LocalDateTime

data class ProductCategoryProjection @QueryProjection constructor(
    // Product 필드
    val id: Long,
    val created: LocalDateTime,
    val modified: LocalDateTime,
    val isRemoved: Boolean,
    val name: String,
    val subtitle: String,
    val code: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val productDescription: String,
    val position: Int,
    val status: Int,
    val stock: Int,
    val categoryId: Long,
    val reviewCount: Int,
    val naverPartner: Boolean,
    val naverPartnerTitle: String,
    val minimumStockLevel: Int,
    val productPg: Boolean,
    val pgSellingPrice: BigDecimal,
    val naverAttribute: String,
    val naverPartnerTitlePg: String,
    val reviewCountPg: Int,
    val maximumStockLevel: Int,
    val stockQuantity: Int,

    // Category 필드
    val categoryCreated: LocalDateTime,
    val categoryModified: LocalDateTime,
    val title: String,
    val slug: String,
    val thumbnail: String,
    val description: String,
    val description1: String,
    val lft: Int,
    val rght: Int,
    val treeId: Int,
    val level: Int,
    val parentId: Long,
    val discountRate: BigDecimal,
    val categoryPg: Boolean,
    val pgDiscountRate: BigDecimal,
    val naverSearchTag: String,
    val naverBrandName: String,
    val naverMakerName: String,
)