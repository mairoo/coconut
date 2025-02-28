package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.dao.DataIntegrityViolationException
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
    ): Category =
        try {
            categoryRepository.save(
                Category.of(
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
            )
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(CatalogErrorCode.PRODUCT_ALREADY_EXISTS)
    }

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
    fun updateBasicInfo(
        id: Long,
        request: CategoryBasicInfoUpdateRequest,
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateBasicInfo(
                newTitle = request.title,
                newSlug = request.slug
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updateDescriptions(
        id: Long,
        request: CategoryDescriptionUpdateRequest,
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateDescriptions(
                newDescription = request.description,
                newDescription1 = request.description1,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updateDiscountRate(
        id: Long,
        request: CategoryDiscountRateUpdateRequest,
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateDiscountRate(
                newDiscountRate = request.discountRate,
                newPgDiscountRate = request.pgDiscountRate,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updatePgStatus(
        id: Long,
        request: CategoryPgStatusUpdateRequest,
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updatePgStatus(
                newPg = request.pg,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updateNaverInfo(
        id: Long,
        request: CategoryNaverInfoUpdateRequest,
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateNaverInfo(
                newNaverSearchTag = request.naverSearchTag,
                newNaverBrandName = request.naverBrandName,
                newNaverMakerName = request.naverMakerName,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun deleteById(
        id: Long,
    ): Unit =
        categoryRepository.deleteById(id)
}