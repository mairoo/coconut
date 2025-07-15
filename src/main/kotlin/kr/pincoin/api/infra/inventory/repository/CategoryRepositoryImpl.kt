package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.repository.CategoryRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
import kr.pincoin.api.infra.inventory.mapper.toModelList
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
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

    override fun findById(
        id: Long,
    ): Category? =
        queryRepository.findById(id)?.toModel()

    override fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria,
    ): Category? =
        queryRepository.findCategory(categoryId, criteria)?.toModel()

    override fun findCategory(
        criteria: CategorySearchCriteria,
    ): Category? =
        queryRepository.findCategory(criteria)?.toModel()

    override fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<Category> =
        queryRepository.findCategories(criteria).toModelList()
}