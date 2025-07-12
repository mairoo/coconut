package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.repository.CategoryRepository
import org.springframework.stereotype.Repository

@Repository
class CategoryRepositoryImpl(
    private val jpaRepository: CategoryJpaRepository,
    private val queryRepository: CategoryQueryRepository,
) : CategoryRepository {
}