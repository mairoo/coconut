package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.infra.catalog.entity.CategoryEntity
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CategoryQueryRepository {
    fun findCategory(
        id: Long,
        criteria: CategorySearchCriteria,
    ): CategoryEntity?

    fun findCategory(
        slug: String,
        criteria: CategorySearchCriteria,
    ): CategoryEntity?

    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<CategoryEntity>

    fun findCategories(
        criteria: CategorySearchCriteria,
        pageable: Pageable,
    ): Page<CategoryEntity>
}