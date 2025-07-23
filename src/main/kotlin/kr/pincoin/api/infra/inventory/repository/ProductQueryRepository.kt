package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.infra.inventory.entity.ProductEntity
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria

interface ProductQueryRepository {
    fun findById(
        id: Long,
    ): ProductEntity?

    fun findProduct(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): ProductEntity?

    fun findProduct(
        criteria: ProductSearchCriteria,
    ): ProductEntity?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductEntity>
}