package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria

interface CategoryRepository {
    fun save(
        category: Category,
    ): Category

    fun findById(
        id: Long,
    ): Category?

    fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria,
    ): Category?

    fun findCategory(
        criteria: CategorySearchCriteria,
    ): Category?

    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category>
}