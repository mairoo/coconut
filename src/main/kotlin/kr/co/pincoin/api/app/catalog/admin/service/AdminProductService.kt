package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.app.catalog.admin.request.ProductCreateRequest
import kr.co.pincoin.api.app.catalog.admin.request.ProductSearchRequest
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.service.ProductService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminProductService(
    private val productService: ProductService,
) {
    fun createProduct(
        request: ProductCreateRequest,
    ): Product =
        productService.createProduct(request)

    fun getProduct(
        id: Long,
        request: ProductSearchRequest,
    ): Product =
        productService.getProduct(
            id, ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                pg = request.pg,
                status = request.status,
                isRemoved = request.isRemoved,
                stock = request.stock,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        ) ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    fun getProduct(
        code: String,
        request: ProductSearchRequest,
    ): Product =
        productService.getProduct(
            code, ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                pg = request.pg,
                status = request.status,
                isRemoved = request.isRemoved,
                stock = request.stock,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        ) ?: throw BusinessException(CatalogErrorCode.PRODUCT_NOT_FOUND)

    fun getProducts(
        request: ProductSearchRequest,
    ): List<Product> =
        productService.getProducts(
            ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                pg = request.pg,
                status = request.status,
                isRemoved = request.isRemoved,
                stock = request.stock,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        )

    fun getProducts(
        request: ProductSearchRequest,
        pageable: Pageable,
    ): Page<Product> =
        productService.getProducts(
            ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                pg = request.pg,
                status = request.status,
                isRemoved = request.isRemoved,
                stock = request.stock,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            ), pageable
        )

    fun updateBasicInfo(
        id: Long,
        name: String? = null,
        subtitle: String? = null,
        code: String? = null
    ): Product =
        productService.updateBasicInfo(id, name, subtitle, code)

    fun updatePriceInfo(
        id: Long,
        listPrice: BigDecimal? = null,
        sellingPrice: BigDecimal? = null,
        pgSellingPrice: BigDecimal? = null
    ): Product =
        productService.updatePriceInfo(id, listPrice, sellingPrice, pgSellingPrice)

    fun updatePgStatus(
        id: Long,
        pg: Boolean? = null
    ): Product =
        productService.updatePgStatus(id, pg)

    fun updateStatus(
        id: Long,
        status: ProductStatus? = null
    ): Product =
        productService.updateStatus(id, status)

    fun updateStockStatus(
        id: Long,
        stock: ProductStock? = null
    ): Product =
        productService.updateStockStatus(id, stock)

    fun updateStockQuantity(
        id: Long,
        stockQuantity: Int? = null
    ): Product =
        productService.updateStockQuantity(id, stockQuantity)

    fun updateStockLevels(
        id: Long,
        minimumStockLevel: Int? = null,
        maximumStockLevel: Int? = null
    ): Product =
        productService.updateStockLevels(id, minimumStockLevel, maximumStockLevel)

    fun increaseReviewCount(
        id: Long
    ): Product =
        productService.increaseReviewCount(id)

    fun increaseReviewCountPg(
        id: Long
    ): Product =
        productService.increaseReviewCountPg(id)
}