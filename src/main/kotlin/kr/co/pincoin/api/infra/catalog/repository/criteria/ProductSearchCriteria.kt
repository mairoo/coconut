package kr.co.pincoin.api.infra.catalog.repository.criteria

import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import java.math.BigDecimal

data class ProductSearchCriteria(
    // 상품 필드
    val name: String? = null,
    val subtitle: String? = null,
    val code: String? = null,
    val categoryId: Long? = null,
    val listPrice: BigDecimal? = null,
    val pg: Boolean? = null,
    val status: ProductStatus? = null,
    val isRemoved: Boolean? = null,
    val stock: ProductStock? = null,

    // 카테고리 필드
    val categoryTitle: String? = null,
    val categorySlug: String? = null,
    val categoryPg: Boolean? = null
)