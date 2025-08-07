package kr.pincoin.api.infra.inventory.repository.criteria

import kr.pincoin.api.app.inventory.admin.request.AdminCategorySearchRequest
import kr.pincoin.api.app.inventory.open.request.OpenCategorySearchRequest
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
) {
    companion object {
        fun from(request: AdminCategorySearchRequest) = CategorySearchCriteria(
            categoryId = request.categoryId,
            title = request.title,
            slug = request.slug,
            description = request.description,
            description1 = request.description1,
            lft = request.lft,
            rght = request.rght,
            treeId = request.treeId,
            level = request.level,
            parentId = request.parentId,
            discountRate = request.discountRate,
            pg = request.pg,
            pgDiscountRate = request.pgDiscountRate,
            naverSearchTag = request.naverSearchTag,
            naverBrandName = request.naverBrandName,
            naverMakerName = request.naverMakerName,
        )

        fun from(request: OpenCategorySearchRequest) = CategorySearchCriteria(
            categoryId = request.categoryId,
            title = request.title,
            slug = request.slug,
            description = request.description,
            description1 = request.description1,
            treeId = request.treeId,
            level = request.level,
            parentId = request.parentId,
            discountRate = request.discountRate,
            pg = request.pg,
            pgDiscountRate = request.pgDiscountRate,
            naverSearchTag = request.naverSearchTag,
            naverBrandName = request.naverBrandName,
            naverMakerName = request.naverMakerName,
        )
    }
}