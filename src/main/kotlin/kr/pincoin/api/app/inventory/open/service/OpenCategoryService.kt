package kr.pincoin.api.app.inventory.open.service

import kr.pincoin.api.app.inventory.open.request.OpenCategorySearchRequest
import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.service.CategoryService
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
import org.springframework.stereotype.Service

@Service
class OpenCategoryService(
    private val categoryService: CategoryService,
) {
    /**
     * 카테고리 목록을 조회합니다 (페이징 없음)
     */
    fun getCategoryList(
        request: OpenCategorySearchRequest,
    ): List<Category> =
        categoryService.findCategories(
            CategorySearchCriteria.from(request)
        )

    /**
     * 카테고리의 상세 정보를 조회합니다.
     */
    fun getCategory(
        categoryId: Long,
    ): Category =
        categoryService.findCategory(
            categoryId,
            CategorySearchCriteria(),
        )

    /**
     * 조건에 맞는 카테고리 정보를 조회합니다.
     */
    fun getCategory(
        request: OpenCategorySearchRequest,
    ): Category =
        categoryService.findCategory(
            CategorySearchCriteria.from(request)
        )
}