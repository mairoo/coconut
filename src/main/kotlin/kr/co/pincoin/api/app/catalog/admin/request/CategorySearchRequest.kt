package kr.co.pincoin.api.app.catalog.admin.request

data class CategorySearchRequest(
    // 카테고리 필드
    val categoryTitle: String? = null,
    val categorySlug: String? = null,
    val categoryPg: Boolean? = null,
)