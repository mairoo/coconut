package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.domain.inventory.repository.ProductRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
import kr.pincoin.api.infra.inventory.mapper.toModelList
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
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

    override fun findById(
        id: Long,
    ): Product? =
        queryRepository.findById(id)?.toModel()

    override fun findProduct(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): Product? =
        queryRepository.findProduct(productId, criteria)?.toModel()

    override fun findProduct(
        criteria: ProductSearchCriteria,
    ): Product? =
        queryRepository.findProduct(criteria)?.toModel()

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<Product> =
        queryRepository.findProducts(criteria).toModelList()
}