package kr.co.pincoin.api.app.catalog.member.request

import java.math.BigDecimal

data class ProductSearchRequest(
    // 상품 필드
    val name: String? = null,
    val subtitle: String? = null,
    val code: String? = null,
    val categoryId: Long? = null,
    val listPrice: BigDecimal? = null,

    // 카테고리 필드
    val categoryTitle: String? = null,
    val categorySlug: String? = null,
    val categoryPg: Boolean? = null,
)
