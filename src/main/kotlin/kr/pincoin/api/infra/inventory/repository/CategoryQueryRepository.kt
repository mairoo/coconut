package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.CategoryEntity
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria

interface CategoryQueryRepository {
    fun findById(
        id: Long,
    ): CategoryEntity?

    fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria,
    ): CategoryEntity?

    fun findCategory(
        criteria: CategorySearchCriteria,
    ): CategoryEntity?

    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<CategoryEntity>
}