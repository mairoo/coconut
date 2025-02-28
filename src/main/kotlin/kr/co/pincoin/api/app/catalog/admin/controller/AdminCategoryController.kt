package kr.co.pincoin.api.app.catalog.admin.controller

import jakarta.validation.Valid
import kr.co.pincoin.api.app.catalog.admin.request.*
import kr.co.pincoin.api.app.catalog.admin.response.CategoryResponse
import kr.co.pincoin.api.app.catalog.admin.service.AdminCategoryService
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/categories")
class AdminCategoryController(
    private val adminCategoryService: AdminCategoryService,
) {
    /**
     * 카테고리 목록을 페이징하여 조회합니다.
     *
     * @param request 카테고리 검색 조건
     * @param pageable 페이징 정보
     * @return 페이징된 카테고리 목록 응답
     */
    @GetMapping
    fun searchCategories(
        request: CategorySearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<Page<CategoryResponse>>> =
        adminCategoryService.getCategories(request, pageable)
            .map { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리 ID로 특정 카테고리를 조회합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 검색 조건
     * @return 카테고리 정보 응답
     */
    @GetMapping("/{id}")
    fun getCategory(
        @PathVariable id: Long,
        request: CategorySearchRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.getCategory(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리 슬러그로 특정 카테고리를 조회합니다.
     *
     * @param slug 카테고리 슬러그
     * @param request 카테고리 검색 조건
     * @return 카테고리 정보 응답
     */
    @GetMapping("/by-slug/{slug}")
    fun getCategoryBySlug(
        @PathVariable slug: String,
        request: CategorySearchRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.getCategory(slug, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 새로운 카테고리를 생성합니다.
     *
     * @param request 카테고리 생성 요청 정보
     * @return 생성된 카테고리 정보 응답
     */
    @PostMapping
    fun createCategory(
        @RequestBody @Valid request: CategoryCreateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.createCategory(request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 기본 정보를 수정합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 기본 정보 수정 요청
     * @return 수정된 카테고리 정보 응답
     */
    @PatchMapping("/{id}")
    fun updateBasicInfo(
        @PathVariable id: Long,
        @RequestBody @Valid request: CategoryBasicInfoUpdateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.updateBasicInfo(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 설명 정보를 수정합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 설명 수정 요청
     * @return 수정된 카테고리 정보 응답
     */
    @PatchMapping("/{id}/descriptions")
    fun updateDescriptions(
        @PathVariable id: Long,
        @RequestBody @Valid request: CategoryDescriptionUpdateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.updateDescriptions(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 가격 정보를 수정합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 가격 정보 수정 요청
     * @return 수정된 카테고리 정보 응답
     */
    @PatchMapping("/{id}/discount-rate")
    fun updatePriceInfo(
        @PathVariable id: Long,
        @RequestBody @Valid request: CategoryDiscountRateUpdateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.updateDiscountRate(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 PG 상태 정보를 수정합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 PG 상태 수정 요청
     * @return 수정된 카테고리 정보 응답
     */
    @PatchMapping("/{id}/pg-status")
    fun updatePgStatus(
        @PathVariable id: Long,
        @RequestBody @Valid request: CategoryPgStatusUpdateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.updatePgStatus(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 네이버 정보를 수정합니다.
     *
     * @param id 카테고리 ID
     * @param request 카테고리 네이버 정보 수정 요청
     * @return 수정된 카테고리 정보 응답
     */
    @PatchMapping("/{id}/naver-ad")
    fun updateNaverInfo(
        @PathVariable id: Long,
        @RequestBody @Valid request: CategoryNaverInfoUpdateRequest,
    ): ResponseEntity<ApiResponse<CategoryResponse>> =
        adminCategoryService.updateNaverInfo(id, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }


    @DeleteMapping("/{id}")
    fun deleteCategory(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<Unit>> =
        adminCategoryService.deleteCategory(id)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}