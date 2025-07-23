package kr.pincoin.api.domain.inventory.service

import kr.pincoin.api.domain.inventory.error.CategoryErrorCode
import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.repository.CategoryRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class CategoryService(
    private val categoryRepository: CategoryRepository,
) {
    fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria,
    ): Category =
        categoryRepository.findCategory(categoryId, criteria)
            ?: throw BusinessException(CategoryErrorCode.NOT_FOUND)

    fun findCategory(
        criteria: CategorySearchCriteria,
    ): Category =
        categoryRepository.findCategory(criteria)
            ?: throw BusinessException(CategoryErrorCode.NOT_FOUND)

    fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category> =
        categoryRepository.findCategories(criteria)

    @Transactional
    fun createCategory(category: Category): Category =
        categoryRepository.save(category)

    @Transactional
    fun updateCategory(category: Category): Category =
        categoryRepository.save(category)
}