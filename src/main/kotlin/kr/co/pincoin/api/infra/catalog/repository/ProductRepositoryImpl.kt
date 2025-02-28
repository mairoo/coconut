package kr.co.pincoin.api.infra.catalog.repository

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.infra.catalog.mapper.toEntity
import kr.co.pincoin.api.infra.catalog.mapper.toModel
import kr.co.pincoin.api.infra.catalog.mapper.toModelList
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
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
    ): Product? =
        queryRepository.findProduct(id, criteria)?.toModel()

    override fun findProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): Product? =
        queryRepository.findProduct(code, criteria)?.toModel()

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<Product> =
        queryRepository.findProducts(criteria).toModelList()

    override fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<Product> =
        queryRepository.findProducts(criteria, pageable).map { it.toModel() }
}