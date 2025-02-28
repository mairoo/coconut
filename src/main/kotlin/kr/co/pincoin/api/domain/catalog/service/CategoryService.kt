package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.CategoryCreateRequest
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
    fun createCategory(
        request: CategoryCreateRequest,
    ): Category {
        val category = Category.of(
            title = request.title,
            slug = request.slug,
            thumbnail = request.thumbnail,
            description = request.description,
            description1 = request.description1,
            discountRate = request.discountRate,
            pg = request.pg,
            pgDiscountRate = request.pgDiscountRate,
            naverSearchTag = request.naverSearchTag,
            naverBrandName = request.naverBrandName,
            naverMakerName = request.naverMakerName,
        )
        return categoryRepository.save(category)
    }

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

    @Transactional
    fun deleteById(
        id: Long,
    ): Unit =
        categoryRepository.deleteById(id)
}