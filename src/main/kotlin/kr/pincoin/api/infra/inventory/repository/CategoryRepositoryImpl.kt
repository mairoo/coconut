package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Category
import kr.pincoin.api.domain.inventory.repository.CategoryRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
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
}