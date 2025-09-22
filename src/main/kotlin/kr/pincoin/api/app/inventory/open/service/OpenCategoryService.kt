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
        categoryService.find(
            CategorySearchCriteria.from(request)
        )

    /**
     * 카테고리의 상세 정보를 조회합니다.
     */
    fun getCategory(
        categoryId: Long,
    ): Category =
        categoryService.get(
            categoryId,
            CategorySearchCriteria(),
        )

    /**
     * 조건에 맞는 카테고리 정보를 조회합니다.
     */
    fun getCategory(
        slug: String,
    ): Category =
        categoryService.get(
            CategorySearchCriteria.from(
                OpenCategorySearchRequest(
                    slug = slug,
                )
            )
        )
}