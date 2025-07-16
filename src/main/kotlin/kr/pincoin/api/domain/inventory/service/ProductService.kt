package kr.pincoin.api.domain.inventory.service

import kr.pincoin.api.domain.inventory.error.ProductErrorCode
import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.domain.inventory.repository.ProductRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun findProduct(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): Product =
        productRepository.findProduct(productId, criteria)
            ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

    fun findProduct(
        criteria: ProductSearchCriteria,
    ): Product =
        productRepository.findProduct(criteria)
            ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

    fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<Product> =
        productRepository.findProducts(criteria)

    @Transactional
    fun createProduct(product: Product): Product =
        productRepository.save(product)

    @Transactional
    fun updateProduct(product: Product): Product =
        productRepository.save(product)
}