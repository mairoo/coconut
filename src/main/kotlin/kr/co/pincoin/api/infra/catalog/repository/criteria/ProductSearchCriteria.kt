package kr.co.pincoin.api.infra.catalog.repository.criteria

import java.math.BigDecimal

data class ProductSearchCriteria(
    // 상품 필드
    val name: String? = null,
    val code: String? = null,
    val storeId: Long? = null,
    val categoryId: Long? = null,
    val minSellingPrice: BigDecimal? = null,
    val maxSellingPrice: BigDecimal? = null,
    val pg: Boolean? = null,
    val status: Int? = null,
    val isRemoved: Boolean? = null,
    val inStock: Boolean? = null, // stock > 0

    // 카테고리 필드
    val categoryTitle: String? = null,
    val categorySlug: String? = null,
    val categoryPg: Boolean? = null
)