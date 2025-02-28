package kr.co.pincoin.api.app.catalog.member.service

import kr.co.pincoin.api.app.catalog.member.request.CategorySearchRequest
import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.service.CategoryService
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

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
        ) ?: throw IllegalArgumentException("Category not found")

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
        ) ?: throw IllegalArgumentException("Category not found")

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