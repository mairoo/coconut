package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.domain.inventory.repository.ProductRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
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
        productId: Long,
    ): Product? =
        queryRepository.findById(productId)?.toModel()
}