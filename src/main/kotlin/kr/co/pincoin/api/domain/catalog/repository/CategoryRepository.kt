package kr.co.pincoin.api.domain.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface CategoryRepository {
    fun save(
        category: Category,
    ): Category

    fun findCategory(
        id: Long,
        criteria: CategorySearchCriteria,
    ): Category?

    fun findCategory(
        slug: String,
        criteria: CategorySearchCriteria,
    ): Category?

    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category>

    fun findCategories(
        criteria: CategorySearchCriteria,
        pageable: Pageable,
    ): Page<Category>

    fun deleteById(
        id: Long,
    )
}