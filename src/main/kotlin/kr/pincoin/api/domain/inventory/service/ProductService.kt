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
    fun get(
        id: Long,
    ): Product =
        productRepository.findById(id)
            ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

    fun get(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): Product =
        productRepository.findProduct(productId, criteria)
            ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

    fun get(
        criteria: ProductSearchCriteria,
    ): Product =
        productRepository.findProduct(criteria)
            ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

    fun find(
        criteria: ProductSearchCriteria,
    ): List<Product> =
        productRepository.findProducts(criteria)

    @Transactional
    fun save(product: Product): Product =
        productRepository.save(product)
}