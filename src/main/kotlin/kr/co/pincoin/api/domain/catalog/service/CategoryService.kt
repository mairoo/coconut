package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun createCategory(category: Category): Category =
        categoryRepository.save(category)

    @Transactional
    fun updateCategory(category: Category): Category =
        categoryRepository.save(category)

    fun getCategory(
        id: Long,
        criteria: CategorySearchCriteria,
    ): Category? =
        categoryRepository.findCategory(id, criteria)

    fun getCategory(
        slug: String,
        criteria: CategorySearchCriteria,
    ): Category? =
        categoryRepository.findCategory(slug, criteria)

    fun getCategories(
        criteria: CategorySearchCriteria,
    ): List<Category> =
        categoryRepository.findCategories(criteria)

    fun getCategories(
        criteria: CategorySearchCriteria,
        pageable: Pageable
    ): Page<Category> =
        categoryRepository.findCategories(criteria, pageable)
}