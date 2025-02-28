package kr.co.pincoin.api.domain.catalog.service

import kr.co.pincoin.api.app.catalog.admin.request.ProductCreateRequest
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
import java.math.BigDecimal

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
        name: String? = null,
        subtitle: String? = null,
        code: String? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateBasicInfo(
                newName = name,
                newSubtitle = subtitle,
                newCode = code
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updatePriceInfo(
        id: Long,
        listPrice: BigDecimal? = null,
        sellingPrice: BigDecimal? = null,
        pgSellingPrice: BigDecimal? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updatePriceInfo(
                newListPrice = listPrice,
                newSellingPrice = sellingPrice,
                newPgSellingPrice = pgSellingPrice
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updatePgStatus(
        id: Long,
        pg: Boolean? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updatePgStatus(
                newPg = pg
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)


    @Transactional
    fun updateStatus(
        id: Long,
        status: ProductStatus? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStatus(
                newStatus = status
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockStatus(
        id: Long,
        stock: ProductStock? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockStatus(
                newStock = stock
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockQuantity(
        id: Long,
        stockQuantity: Int? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockQuantity(
                newStockQuantity = stockQuantity
            )
            ?.let { productRepository.save(it) }
            ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    @Transactional
    fun updateStockLevels(
        id: Long,
        minimumStockLevel: Int? = null,
        maximumStockLevel: Int? = null
    ): Product =
        productRepository.findProduct(id, ProductSearchCriteria())
            ?.updateStockLevels(
                newMinimumStockLevel = minimumStockLevel,
                newMaximumStockLevel = maximumStockLevel
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