package kr.pincoin.api.app.inventory.admin.service

import kr.pincoin.api.app.inventory.admin.request.AdminCategorySearchRequest
import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.service.CategoryService
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminCategoryService(
    private val categoryService: CategoryService,
) {
    /**
     * 카테고리 목록을 조회합니다 (페이징 없음)
     */
    fun getCategoryList(
        request: AdminCategorySearchRequest,
    ): List<Category> =
        categoryService.findCategories(CategorySearchCriteria.from(request))

    /**
     * 카테고리의 상세 정보를 조회합니다.
     */
    fun getCategory(
        categoryId: Long,
    ): Category =
        categoryService.findCategory(categoryId, CategorySearchCriteria())

    /**
     * 조건에 맞는 카테고리 정보를 조회합니다.
     */
    fun getCategory(
        request: AdminCategorySearchRequest,
    ): Category =
        categoryService.findCategory(CategorySearchCriteria.from(request))

    /**
     * 카테고리를 생성합니다.
     */
    fun createCategory(
        category: Category,
    ): Category =
        categoryService.createCategory(category)

    /**
     * 카테고리 정보를 업데이트합니다.
     */
    fun updateCategory(
        category: Category,
    ): Category =
        categoryService.updateCategory(category)
}