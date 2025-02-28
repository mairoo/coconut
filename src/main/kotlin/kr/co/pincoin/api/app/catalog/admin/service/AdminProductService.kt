package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.service.ProductService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

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
        request: ProductBasicInfoUpdateRequest,
    ): Product =
        productService.updateBasicInfo(id, request)

    fun updatePriceInfo(
        id: Long,
        request: ProductPriceUpdateRequest,
    ): Product =
        productService.updatePriceInfo(id, request)

    fun updatePgStatus(
        id: Long,
        request: ProductPgStatusUpdateRequest,
    ): Product =
        productService.updatePgStatus(id, request)

    fun updateStatus(
        id: Long,
        request: ProductStatusUpdateRequest,
    ): Product =
        productService.updateStatus(id, request)

    fun updateStockStatus(
        id: Long,
        request: ProductStockStatusUpdateRequest,
    ): Product =
        productService.updateStockStatus(id, request)

    fun updateStockQuantity(
        id: Long,
        request: ProductStockQuantityUpdateRequest,
    ): Product =
        productService.updateStockQuantity(id, request)

    fun updateStockLevels(
        id: Long,
        request: ProductStockLevelsUpdateRequest,
    ): Product =
        productService.updateStockLevels(id, request)

    fun increaseReviewCount(
        id: Long
    ): Product =
        productService.increaseReviewCount(id)

    fun increaseReviewCountPg(
        id: Long
    ): Product =
        productService.increaseReviewCountPg(id)
}