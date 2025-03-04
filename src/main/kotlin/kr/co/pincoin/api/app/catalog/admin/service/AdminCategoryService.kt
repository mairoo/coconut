package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.service.CategoryService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminCategoryService(
    private val categoryService: CategoryService,
) {
    fun createCategory(
        request: CategoryCreateRequest,
    ): Category =
        categoryService.createCategory(request)

    fun getCategory(
        id: Long,
        request: CategorySearchRequest,
    ): Category =
        categoryService.getCategory(
            id, CategorySearchCriteria(
                title = request.categoryTitle,
                slug = request.categorySlug,
                pg = request.categoryPg
            )
        ) ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    fun getCategory(
        slug: String,
        request: CategorySearchRequest,
    ): Category =
        categoryService.getCategory(
            slug, CategorySearchCriteria(
                title = request.categoryTitle,
                slug = request.categorySlug,
                pg = request.categoryPg
            )
        ) ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    fun getCategories(
        request: CategorySearchRequest,
    ): List<Category> =
        categoryService.getCategories(
            CategorySearchCriteria(
                title = request.categoryTitle,
                slug = request.categorySlug,
                pg = request.categoryPg
            )
        )

    fun getCategories(
        request: CategorySearchRequest,
        pageable: Pageable,
    ): Page<Category> =
        categoryService.getCategories(
            CategorySearchCriteria(
                title = request.categoryTitle,
                slug = request.categorySlug,
                pg = request.categoryPg
            ), pageable
        )

    fun update(
        id: Long,
        request: CategoryBasicInfoUpdateRequest,
    ): Category =
        categoryService.update(id, request)

    fun updateDescriptions(
        id: Long,
        request: CategoryDescriptionUpdateRequest,
    ): Category =
        categoryService.updateDescriptions(id, request)

    fun updateDiscountRate(
        id: Long,
        request: CategoryDiscountRateUpdateRequest,
    ): Category =
        categoryService.updateDiscountRate(id, request)

    fun updatePgStatus(
        id: Long,
        request: CategoryPgStatusUpdateRequest,
    ): Category =
        categoryService.updatePgStatus(id, request)

    fun updateNaverInfo(
        id: Long,
        request: CategoryNaverInfoUpdateRequest,
    ): Category =
        categoryService.updateNaverInfo(id, request)

    fun deleteCategory(
        id: Long,
    ): Unit =
        categoryService.deleteById(id)
}