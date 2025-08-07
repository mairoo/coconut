package kr.pincoin.api.app.inventory.open.service

import kr.pincoin.api.app.inventory.open.request.OpenProductSearchRequest
import kr.pincoin.api.domain.inventory.model.Product
import kr.pincoin.api.domain.inventory.service.ProductService
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
import org.springframework.stereotype.Service

@Service
class OpenProductService(
    private val productService: ProductService,
) {
    /**
     * 상품 목록을 조회합니다 (페이징 없음)
     */
    fun getProductList(
        request: OpenProductSearchRequest,
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
        request: OpenProductSearchRequest,
    ): Product =
        productService.findProduct(
            ProductSearchCriteria.from(request)
        )
}