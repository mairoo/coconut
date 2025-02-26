package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.domain.catalog.repository.CategoryRepository
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository,
    private val queryRepository: CategoryQueryRepository,
) : CategoryRepository {
}