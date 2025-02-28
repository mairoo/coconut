package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductQueryRepository {
    fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): ProductCategoryProjection?

    fun findProduct(
        code: String,
        criteria: ProductSearchCriteria
    ): ProductCategoryProjection?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductCategoryProjection>

    fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductCategoryProjection>
}