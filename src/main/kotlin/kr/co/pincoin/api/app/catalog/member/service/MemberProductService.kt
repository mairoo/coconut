package kr.co.pincoin.api.app.catalog.member.service

import kr.co.pincoin.api.app.catalog.member.request.ProductSearchRequest
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.service.ProductService
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class MemberProductService(
    private val productService: ProductService,
) {
    fun getProduct(
        id: Long,
        request: ProductSearchRequest
    ): ProductCategoryProjection =
        productService.getProduct(
            id, ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                status = ProductStatus.ENABLED,
                isRemoved = false,
                stock = ProductStock.IN_STOCK,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        ) ?: throw IllegalArgumentException("Product with id $id not found")

    fun getProduct(
        code: String,
        request: ProductSearchRequest,
    ): ProductCategoryProjection =
        productService.getProduct(
            code, ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                status = ProductStatus.ENABLED,
                isRemoved = false,
                stock = kr.co.pincoin.api.domain.catalog.enums.ProductStock.IN_STOCK,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        ) ?: throw IllegalArgumentException("Product with code $code not found")

    fun getProducts(
        request: ProductSearchRequest,
    ): List<ProductCategoryProjection> =
        productService.getProducts(
            ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                status = ProductStatus.ENABLED,
                isRemoved = false,
                stock = kr.co.pincoin.api.domain.catalog.enums.ProductStock.IN_STOCK,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        )

    fun getProducts(
        request: ProductSearchRequest,
        pageable: Pageable,
    ): Page<ProductCategoryProjection> =
        productService.getProducts(
            ProductSearchCriteria(
                name = request.name,
                subtitle = request.subtitle,
                code = request.code,
                categoryId = request.categoryId,
                listPrice = request.listPrice,
                status = ProductStatus.ENABLED,
                isRemoved = false,
                stock = kr.co.pincoin.api.domain.catalog.enums.ProductStock.IN_STOCK,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            ), pageable
        )
}