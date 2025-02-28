package kr.co.pincoin.api.domain.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductRepository {
    fun save(
        product: Product,
    ): Product

    fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): ProductProjection?

    fun findProduct(
        code: String,
        criteria: ProductSearchCriteria
    ): ProductProjection?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductProjection>

    fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductProjection>
}