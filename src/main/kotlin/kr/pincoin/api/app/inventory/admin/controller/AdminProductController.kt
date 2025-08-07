package kr.pincoin.api.app.inventory.admin.controller

import kr.pincoin.api.app.inventory.admin.request.AdminProductSearchRequest
import kr.pincoin.api.app.inventory.admin.response.AdminProductResponse
import kr.pincoin.api.app.inventory.admin.service.AdminProductService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/products")
class AdminProductController(
    private val adminProductService: AdminProductService,
) {
    /**
     * 상품 목록을 조건별로 검색합니다.
     */
    @GetMapping
    fun getProductList(
        request: AdminProductSearchRequest,
    ): ResponseEntity<ApiResponse<List<AdminProductResponse>>> =
        adminProductService.getProductList(request)
            .map(AdminProductResponse::from)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 상세 정보를 조회합니다.
     */
    @GetMapping("/{productId}")
    fun getProduct(
        @PathVariable productId: Long,
    ): ResponseEntity<ApiResponse<AdminProductResponse>> =
        adminProductService.getProduct(productId)
            .let { AdminProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}