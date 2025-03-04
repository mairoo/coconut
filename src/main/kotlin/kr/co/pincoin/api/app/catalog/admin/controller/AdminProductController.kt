package kr.co.pincoin.api.app.catalog.admin.controller

import jakarta.validation.Valid
import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.app.catalog.admin.response.ProductResponse
import kr.co.pincoin.api.app.catalog.admin.service.AdminProductService
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/products")
class AdminProductController(
    private val adminProductService: AdminProductService,
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
    ): ResponseEntity<ApiResponse<Page<ProductResponse>>> =
        adminProductService.getProducts(request, pageable)
            .map { ProductResponse.from(it) }
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
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.getProduct(id, request)
            .let { ProductResponse.from(it) }
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
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.getProduct(code, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 새로운 상품을 생성합니다.
     *
     * @param request 상품 생성 요청 정보
     * @return 생성된 상품 정보 응답
     */
    @PostMapping
    fun createProduct(
        @RequestBody @Valid request: ProductCreateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.createProduct(request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 기본 정보를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 기본 정보 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/basic-info")
    fun update(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductBasicInfoUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.update(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 가격 정보를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 가격 정보 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/price-info")
    fun updatePrices(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductPriceUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updatePrices(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 PG 상태 정보를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 PG 상태 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/pg-status")
    fun updatePgStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductPgStatusUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updatePgStatus(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 상태 정보를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 상태 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/status")
    fun updateStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductStatusUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updateStatus(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 재고 상태를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 재고 상태 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/stock-status")
    fun updateStockStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductStockStatusUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updateStockStatus(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 재고 수량을 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 재고 수량 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/stock-quantity")
    fun updateStockQuantity(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductStockQuantityUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updateStockQuantity(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 재고 수준 정보를 수정합니다.
     *
     * @param id 상품 ID
     * @param request 상품 재고 수준 수정 요청
     * @return 수정된 상품 정보 응답
     */
    @PutMapping("/{id}/stock-levels")
    fun updateStockLevels(
        @PathVariable id: Long,
        @RequestBody @Valid request: ProductStockLevelsUpdateRequest,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.updateStockLevels(id, request)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 리뷰 카운트를 증가시킵니다.
     *
     * @param id 상품 ID
     * @return 수정된 상품 정보 응답
     */
    @PatchMapping("/{id}/increase-review-count")
    fun increaseReviewCount(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.increaseReviewCount(id)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 상품의 PG 리뷰 카운트를 증가시킵니다.
     *
     * @param id 상품 ID
     * @return 수정된 상품 정보 응답
     */
    @PatchMapping("/{id}/increase-review-count-pg")
    fun increaseReviewCountPg(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<ProductResponse>> =
        adminProductService.increaseReviewCountPg(id)
            .let { ProductResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}