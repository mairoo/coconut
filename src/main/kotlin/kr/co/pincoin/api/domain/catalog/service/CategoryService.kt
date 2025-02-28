package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.CategoryCreateRequest
import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

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
        title: String? = null,
        slug: String? = null
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateBasicInfo(title, slug)
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updateDescriptions(
        id: Long,
        description: String? = null,
        description1: String? = null
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateDescriptions(
                newDescription = description,
                newDescription1 = description1
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updatePriceInfo(
        id: Long,
        discountRate: BigDecimal? = null,
        pgDiscountRate: BigDecimal? = null
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updatePriceInfo(
                newDiscountRate = discountRate,
                newPgDiscountRate = pgDiscountRate,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updatePgStatus(
        id: Long,
        pg: Boolean? = null
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updatePgStatus(
                newPg = pg,
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun updateNaverInfo(
        id: Long,
        naverSearchTag: String? = null,
        naverBrandName: String? = null,
        naverMakerName: String? = null
    ): Category =
        categoryRepository.findCategory(id, CategorySearchCriteria())
            ?.updateNaverInfo(
                newNaverSearchTag = naverSearchTag,
                newNaverBrandName = naverBrandName,
                newNaverMakerName = naverMakerName
            )
            ?.let { categoryRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.CATEGORY_NOT_FOUND)

    @Transactional
    fun deleteById(
        id: Long,
    ): Unit =
        categoryRepository.deleteById(id)
}