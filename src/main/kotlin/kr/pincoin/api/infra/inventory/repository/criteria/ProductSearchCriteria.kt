package kr.pincoin.api.infra.inventory.repository.criteria

import kr.pincoin.api.app.inventory.admin.request.AdminProductSearchRequest
import kr.pincoin.api.app.inventory.open.request.OpenProductSearchRequest
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
) {
    companion object {
        fun from(request: AdminProductSearchRequest) = ProductSearchCriteria(
            productId = request.productId,
            name = request.name,
            subtitle = request.subtitle,
            code = request.code,
            listPrice = request.listPrice,
            sellingPrice = request.sellingPrice,
            description = request.description,
            position = request.position,
            status = request.status,
            stock = request.stock,
            categoryId = request.categoryId,
            reviewCount = request.reviewCount,
            naverPartner = request.naverPartner,
            naverPartnerTitle = request.naverPartnerTitle,
            minimumStockLevel = request.minimumStockLevel,
            pg = request.pg,
            pgSellingPrice = request.pgSellingPrice,
            naverAttribute = request.naverAttribute,
            naverPartnerTitlePg = request.naverPartnerTitlePg,
            reviewCountPg = request.reviewCountPg,
            maximumStockLevel = request.maximumStockLevel,
            stockQuantity = request.stockQuantity,
            isRemoved = request.isRemoved,
        )

        fun from(request: OpenProductSearchRequest) = ProductSearchCriteria(
            productId = request.productId,
            name = request.name,
            subtitle = request.subtitle,
            code = request.code,
            description = request.description,
            position = request.position,
            status = request.status,
            categoryId = request.categoryId,
            reviewCount = request.reviewCount,
            naverPartner = request.naverPartner,
            naverPartnerTitle = request.naverPartnerTitle,
            pg = request.pg,
            pgSellingPrice = request.pgSellingPrice,
            naverAttribute = request.naverAttribute,
            naverPartnerTitlePg = request.naverPartnerTitlePg,
            reviewCountPg = request.reviewCountPg,
            isRemoved = request.isRemoved,
        )
    }
}