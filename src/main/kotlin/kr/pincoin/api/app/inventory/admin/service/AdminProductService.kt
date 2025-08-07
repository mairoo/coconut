package kr.pincoin.api.app.inventory.admin.service

import kr.pincoin.api.app.inventory.admin.request.AdminProductSearchRequest
import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.domain.inventory.service.ProductService
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminProductService(
    private val productService: ProductService,
) {
    /**
     * 상품 목록을 조회합니다 (페이징 없음)
     */
    fun getProductList(
        request: AdminProductSearchRequest,
    ): List<Product> =
        productService.findProducts(
            ProductSearchCriteria.from(request)
        )

    /**
     * 상품의 상세 정보를 조회합니다.
     */
    fun getProduct(
        productId: Long,
    ): Product =
        productService.findProduct(
            productId,
            ProductSearchCriteria(),
        )

    /**
     * 조건에 맞는 상품 정보를 조회합니다.
     */
    fun getProduct(
        request: AdminProductSearchRequest,
    ): Product =
        productService.findProduct(
            ProductSearchCriteria.from(request)
        )

    /**
     * 상품을 생성합니다.
     */
    fun createProduct(
        product: Product,
    ): Product =
        productService.createProduct(product)

    /**
     * 상품 정보를 업데이트합니다.
     */
    fun updateProduct(
        product: Product,
    ): Product =
        productService.updateProduct(product)
}