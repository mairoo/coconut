package kr.co.pincoin.api.domain.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepository {
    fun save(
        product: Product,
    ): Product

    fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): Product?

    fun findProduct(
        code: String,
        criteria: ProductSearchCriteria
    ): Product?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<Product>

    fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<Product>
}