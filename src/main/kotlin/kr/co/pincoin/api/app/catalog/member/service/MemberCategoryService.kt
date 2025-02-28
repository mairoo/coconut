package kr.co.pincoin.api.app.catalog.member.service

import kr.co.pincoin.api.app.catalog.member.request.CategorySearchRequest
import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.service.CategoryService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MemberCategoryService(
    private val categoryService: CategoryService,
) {
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
}