package kr.pincoin.api.infra.inventory.repository.criteria

import java.math.BigDecimal

data class ProductSearchCriteria(
    val productId: Long? = null,
    val name: String? = null,
    val subtitle: String? = null,
    val code: String? = null,
    val listPrice: BigDecimal? = null,
    val sellingPrice: BigDecimal? = null,
    val description: String? = null,
    val position: Int? = null,
    val status: Int? = null,
    val stock: Int? = null,
    val categoryId: Long? = null,
    val storeId: Long? = null,
    val reviewCount: Int? = null,
    val naverPartner: Boolean? = null,
    val naverPartnerTitle: String? = null,
    val minimumStockLevel: Int? = null,
    val pg: Boolean? = null,
    val pgSellingPrice: BigDecimal? = null,
    val naverAttribute: String? = null,
    val naverPartnerTitlePg: String? = null,
    val reviewCountPg: Int? = null,
    val maximumStockLevel: Int? = null,
    val stockQuantity: Int? = null,
    val isRemoved: Boolean? = null,
)