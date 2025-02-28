package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.infra.catalog.entity.ProductEntity
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable

interface ProductQueryRepository {
    fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): ProductEntity?

    fun findProduct(
        code: String,
        criteria: ProductSearchCriteria
    ): ProductEntity?

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductEntity>

    fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductEntity>
}