package kr.pincoin.api.app.inventory.open.controller

import kr.pincoin.api.app.inventory.open.request.OpenCategorySearchRequest
import kr.pincoin.api.app.inventory.open.response.OpenCategoryResponse
import kr.pincoin.api.app.inventory.open.service.OpenCategoryService
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/open/categories")
class OpenCategoryController(
    private val openCategoryService: OpenCategoryService,
) {
    /**
     * 카테고리 목록 조회
     */
    @GetMapping
    fun getCategories(
        request: OpenCategorySearchRequest,
    ): ResponseEntity<ApiResponse<List<OpenCategoryResponse>>> =
        openCategoryService.findCategories(
            criteria = request.toSearchCriteria(),
        )
            .map { OpenCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리 상세 조회 (ID)
     */
    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long,
        @RequestParam(required = false) storeId: Long?,
    ): ResponseEntity<ApiResponse<OpenCategoryResponse>> =
        openCategoryService.findCategory(
            categoryId = categoryId,
            criteria = CategorySearchCriteria(),
        )
            .let { OpenCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리 상세 조회 (Slug)
     */
    @GetMapping("/slug/{slug}")
    fun getCategoryBySlug(
        @PathVariable slug: String,
        @RequestParam(required = false) storeId: Long?,
    ): ResponseEntity<ApiResponse<OpenCategoryResponse>> =
        openCategoryService.findCategoryBySlug(
            slug = slug,
            storeId = storeId,
        )
            .let { OpenCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}