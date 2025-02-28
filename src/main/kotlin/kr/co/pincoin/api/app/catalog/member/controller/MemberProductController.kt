package kr.co.pincoin.api.app.catalog.member.controller

import kr.co.pincoin.api.app.catalog.member.request.ProductSearchRequest
import kr.co.pincoin.api.app.catalog.member.service.MemberProductService
import kr.co.pincoin.api.app.catalog.response.ProductCategoryResponse
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/products")
class MemberProductController(
    private val memberProductService: MemberProductService,
) {
    /**
     * 상품 목록을 페이징하여 조회합니다.
     *
     * @param request 상품 검색 조건
     * @param pageable 페이징 정보
     * @return 페이징된 상품 목록 응답
     */
    @GetMapping
    fun searchProducts(
        request: ProductSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<ProductCategoryResponse>>> =
        memberProductService.getProducts(request, pageable)
            .map { ProductCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품 ID로 특정 상품을 조회합니다.
     *
     * @param id 상품 ID
     * @param request 상품 검색 조건
     * @return 상품 정보 응답
     */
    @GetMapping("/{id}")
    fun getProduct(
        @PathVariable id: Long,
        request: ProductSearchRequest,
    ): ResponseEntity<ApiResponse<ProductCategoryResponse>> =
        memberProductService.getProduct(id, request)
            .let { ProductCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품 코드로 특정 상품을 조회합니다.
     *
     * @param code 상품 코드
     * @param request 상품 검색 조건
     * @return 상품 정보 응답
     */
    @GetMapping("/by-code/{code}")
    fun getProductByCode(
        @PathVariable code: String,
        request: ProductSearchRequest,
    ): ResponseEntity<ApiResponse<ProductCategoryResponse>> =
        memberProductService.getProduct(code, request)
            .let { ProductCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}