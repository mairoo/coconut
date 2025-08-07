package kr.pincoin.api.app.inventory.admin.controller

import kr.pincoin.api.app.inventory.admin.request.AdminCategorySearchRequest
import kr.pincoin.api.app.inventory.admin.response.AdminCategoryResponse
import kr.pincoin.api.app.inventory.admin.service.AdminCategoryService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/categories")
class AdminCategoryController(
    private val adminCategoryService: AdminCategoryService,
) {
    /**
     * 카테고리 목록을 조건별로 검색합니다.
     */
    @GetMapping
    fun getCategoryList(
        request: AdminCategorySearchRequest,
    ): ResponseEntity<ApiResponse<List<AdminCategoryResponse>>> =
        adminCategoryService.getCategoryList(request)
            .map(AdminCategoryResponse::from)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 카테고리의 상세 정보를 조회합니다.
     */
    @GetMapping("/{categoryId}")
    fun getCategory(
        @PathVariable categoryId: Long,
    ): ResponseEntity<ApiResponse<AdminCategoryResponse>> =
        adminCategoryService.getCategory(categoryId)
            .let { AdminCategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}