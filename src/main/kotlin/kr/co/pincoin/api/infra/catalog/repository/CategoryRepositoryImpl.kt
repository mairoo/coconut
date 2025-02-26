package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Category
import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import kr.co.pincoin.api.infra.catalog.mapper.toEntity
import kr.co.pincoin.api.infra.catalog.mapper.toModel
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