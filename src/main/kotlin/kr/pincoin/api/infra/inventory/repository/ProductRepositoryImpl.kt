package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.repository.ProductRepository
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository,
    private val queryRepository: ProductQueryRepository,
) : ProductRepository {
}