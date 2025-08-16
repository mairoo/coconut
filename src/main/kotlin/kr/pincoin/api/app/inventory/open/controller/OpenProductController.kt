package kr.pincoin.api.app.inventory.open.controller

import kr.pincoin.api.app.inventory.open.request.OpenProductSearchRequest
import kr.pincoin.api.app.inventory.open.response.OpenProductResponse
import kr.pincoin.api.app.inventory.open.service.OpenProductService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/open/products")
class OpenProductController(
    private val openProductService: OpenProductService,
) {
    /**
     * 상품 목록을 조건별로 검색합니다.
     */
    @GetMapping
    fun getProductList(
        request: OpenProductSearchRequest,
    ): ResponseEntity<ApiResponse<List<OpenProductResponse>>> =
        openProductService.getProductList(request)
            .map(OpenProductResponse::from)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 상세 정보를 조회합니다.
     *
     * @param productId 상품 ID
     * @return 상품 상세 정보
     */
    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<ApiResponse<OpenProductResponse>> =
        openProductService.getProduct(productId)
            .let { OpenProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 특정 카테고리의 상품 목록을 조회합니다.
     */
    @GetMapping("/category/{categoryId}")
    fun getProductsByCategory(
        @PathVariable categoryId: Long,
        request: OpenProductSearchRequest,
    ): ResponseEntity<ApiResponse<List<OpenProductResponse>>> =
        openProductService.getProductList(
            request.copy(categoryId = categoryId, status = 0)
        )
            .map(OpenProductResponse::from)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}