package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository
) {
    @Transactional
    fun createProduct(product: Product): Product =
        productRepository.save(product)

    @Transactional
    fun updateProduct(product: Product): Product =
        productRepository.save(product)

    fun getProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): ProductProjection? =
        productRepository.findProduct(id, criteria)

    fun getProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): ProductProjection? =
        productRepository.findProduct(code, criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductProjection> =
        productRepository.findProducts(criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductProjection> =
        productRepository.findProducts(criteria, pageable)
}