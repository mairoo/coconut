package kr.co.pincoin.api.app.catalog.admin.controller

import jakarta.validation.Valid
import kr.co.pincoin.api.app.catalog.admin.request.CategoryCreateRequest
import kr.co.pincoin.api.app.catalog.admin.request.CategorySearchRequest
import kr.co.pincoin.api.app.catalog.admin.service.AdminCategoryService
import kr.co.pincoin.api.app.catalog.response.CategoryResponse
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

    @DeleteMapping("/{id}")
    fun deleteCategory(
        @PathVariable id: Long,
    ): ResponseEntity<ApiResponse<Unit>> =
        adminCategoryService.deleteCategory(id)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}