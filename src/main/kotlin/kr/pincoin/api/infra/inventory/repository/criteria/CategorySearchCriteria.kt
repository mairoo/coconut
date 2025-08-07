package kr.pincoin.api.infra.inventory.repository.criteria

import java.math.BigDecimal

data class CategorySearchCriteria(
    val categoryId: Long? = null,
    val title: String? = null,
    val slug: String? = null,
    val thumbnail: String? = null,
    val description: String? = null,
    val description1: String? = null,
    val lft: Int? = null,
    val rght: Int? = null,
    val storeId: Long? = null,
    val parentId: Long? = null,
    val level: Int? = null,
    val treeId: Int? = null,
    val discountRate: BigDecimal? = null,
    val pg: Boolean? = null,
    val pgDiscountRate: BigDecimal? = null,
    val naverSearchTag: String? = null,
    val naverBrandName: String? = null,
    val naverMakerName: String? = null,
)