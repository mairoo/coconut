package kr.pincoin.api.app.inventory.open.controller

import kr.pincoin.api.app.inventory.open.request.OpenCategorySearchRequest
import kr.pincoin.api.app.inventory.open.response.OpenCategoryResponse
import kr.pincoin.api.app.inventory.open.service.OpenCategoryService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/open/categories")
class OpenCategoryController(
    private val openCategoryService: OpenCategoryService,
) {
    /**
     * 카테고리 목록을 조건별로 검색합니다.
     */
    @GetMapping
    fun getCategoryList(
        request: OpenCategorySearchRequest,
    ): ResponseEntity<ApiResponse<List<OpenCategoryResponse>>> =
        openCategoryService.getCategoryList(request)
            .map(OpenCategoryResponse::from)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 상세 정보를 조회합니다.
     */
    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long,
    ): ResponseEntity<ApiResponse<OpenCategoryResponse>> =
        openCategoryService.getCategory(categoryId)
            .let { OpenCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}