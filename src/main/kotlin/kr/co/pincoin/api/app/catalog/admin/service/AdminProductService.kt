package kr.co.pincoin.api.app.catalog.admin.service

import kr.co.pincoin.api.app.catalog.admin.request.ProductCreateRequest
import kr.co.pincoin.api.app.catalog.admin.request.ProductSearchRequest
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.service.ProductService
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductCategoryProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.lang.IllegalArgumentException

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
    ): ProductCategoryProjection =
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
                pg = request.pg,
                status = request.status,
                isRemoved = request.isRemoved,
                stock = request.stock,
                categoryTitle = request.categoryTitle,
                categorySlug = request.categorySlug,
                categoryPg = request.categoryPg
            )
        ) ?: throw IllegalArgumentException("$code not found")

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
    ): Page<ProductCategoryProjection> =
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
}