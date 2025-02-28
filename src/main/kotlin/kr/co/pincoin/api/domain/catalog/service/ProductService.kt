package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
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

    fun getProduct(
        id: Long,
        criteria: ProductSearchCriteria
    ): Product? =
        productRepository.findProduct(id, criteria)

    fun getProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): Product? =
        productRepository.findProduct(code, criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
    ): List<Product> =
        productRepository.findProducts(criteria)

    fun getProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<Product> =
        productRepository.findProducts(criteria, pageable)

    @Transactional
    fun updateBasicInfo(
        id: Long,
        request: ProductBasicInfoUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateBasicInfo(
                newName = request.name,
                newSubtitle = request.subtitle,
                newCode = request.code,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updatePriceInfo(
        id: Long,
        request: ProductPriceUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updatePriceInfo(
                newListPrice = request.listPrice,
                newSellingPrice = request.sellingPrice,
                newPgSellingPrice = request.pgSellingPrice,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updatePgStatus(
        id: Long,
        request: ProductPgStatusUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updatePgStatus(
                newPg = request.pg,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)


    @Transactional
    fun updateStatus(
        id: Long,
        request: ProductStatusUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStatus(
                newStatus = request.status,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockStatus(
        id: Long,
        request: ProductStockStatusUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockStatus(
                newStock = request.stock,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockQuantity(
        id: Long,
        request: ProductStockQuantityUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockQuantity(
                newStockQuantity = request.stockQuantity,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockLevels(
        id: Long,
        request: ProductStockLevelsUpdateRequest,
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockLevels(
                newMinimumStockLevel = request.minimumStockLevel,
                newMaximumStockLevel = request.maximumStockLevel,
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun increaseReviewCount(
        id: Long
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.increaseReviewCount()
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun increaseReviewCountPg(
        id: Long
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.increaseReviewCountPg()
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)
}