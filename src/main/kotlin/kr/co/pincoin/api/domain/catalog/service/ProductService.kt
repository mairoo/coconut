package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.ProductCreateRequest
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
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
    fun createProduct(
        request: ProductCreateRequest,
    ): Product {
        val product = Product.of(
            categoryId = request.categoryId,
            name = request.name,
            subtitle = request.subtitle,
            code = request.code,
            listPrice = request.listPrice,
            sellingPrice = request.sellingPrice,
            pg = request.pg,
            pgSellingPrice = request.pgSellingPrice,
            description = request.description,
            position = request.position,
            status = ProductStatus.ENABLED,
            stockQuantity = 0,
            stock = ProductStock.IN_STOCK,
            minimumStockLevel = request.minimumStockLevel,
            maximumStockLevel = request.maximumStockLevel,
            reviewCount = 0,
            reviewCountPg = 0,
            naverPartner = request.naverPartner,
            naverPartnerTitle = request.naverPartnerTitle,
            naverPartnerTitlePg = request.naverPartnerTitlePg,
            naverAttribute = request.naverAttribute,
        )
        return productRepository.save(product)
    }

    @Transactional
    fun updateProduct(product: Product): Product =
        productRepository.save(product)

    fun getProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): ProductCategoryProjection? =
        productRepository.findProduct(id, criteria)

    fun getProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): ProductCategoryProjection? =
        productRepository.findProduct(code, criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductCategoryProjection> =
        productRepository.findProducts(criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductCategoryProjection> =
        productRepository.findProducts(criteria, pageable)
}