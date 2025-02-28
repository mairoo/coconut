package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import kr.co.pincoin.api.infra.catalog.mapper.toEntity
import kr.co.pincoin.api.infra.catalog.mapper.toModel
import kr.co.pincoin.api.infra.catalog.mapper.toModelList
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository,
    private val queryRepository: CategoryQueryRepository,
) : CategoryRepository {
    override fun save(
        category: Category,
    ): Category =
        category.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("카테고리 저장 실패")

    override fun findCategory(
        id: Long,
        criteria: CategorySearchCriteria,
    ): Category? =
        queryRepository.findCategory(id, criteria)?.toModel()

    override fun findCategory(
        slug: String,
        criteria: CategorySearchCriteria,
    ): Category? =
        queryRepository.findCategory(slug, criteria)?.toModel()

    override fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category> =
        queryRepository.findCategories(criteria).toModelList()

    override fun findCategories(
        criteria: CategorySearchCriteria,
        pageable: Pageable,
    ): Page<Category> =
        queryRepository.findCategories(criteria, pageable).map { it.toModel() }
}