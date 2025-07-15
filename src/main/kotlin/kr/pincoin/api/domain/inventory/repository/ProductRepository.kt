package kr.pincoin.api.domain.inventory.repository

import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria

interface ProductRepository {
    fun save(
        product: Product,
    ): Product

    fun findById(
        id: Long,
    ): Product?

    fun findProduct(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): Product?

    fun findProduct(
        criteria: ProductSearchCriteria,
    ): Product?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<Product>
}