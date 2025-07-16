package kr.pincoin.api.app.inventory.open.service

import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.service.CategoryService
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OpenCategoryService(
    private val categoryService: CategoryService,
) {
    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category> =
        categoryService.findCategories(criteria)

    fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria = CategorySearchCriteria(),
    ): Category =
        categoryService.findCategory(categoryId, criteria)

    fun findCategoryBySlug(
        slug: String,
        storeId: Long? = null,
    ): Category =
        categoryService.findCategory(
            CategorySearchCriteria(
                slug = slug,
                storeId = storeId,
            )
        )
}