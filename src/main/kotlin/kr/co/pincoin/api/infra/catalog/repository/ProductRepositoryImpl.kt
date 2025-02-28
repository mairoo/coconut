package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.infra.catalog.mapper.toEntity
import kr.co.pincoin.api.infra.catalog.mapper.toModel
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class ProductRepositoryImpl(
    private val jpaRepository: ProductJpaRepository,
    private val queryRepository: ProductQueryRepository,
) : ProductRepository {
    override fun save(
        product: Product,
    ): Product =
        product.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("상품 저장 실패")

    override fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria,
    ): ProductCategoryProjection? =
        queryRepository.findProduct(id, criteria)

    override fun findProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): ProductCategoryProjection? =
        queryRepository.findProduct(code, criteria)

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductCategoryProjection> =
        queryRepository.findProducts(criteria)

    override fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductCategoryProjection> =
        queryRepository.findProducts(criteria, pageable)
}