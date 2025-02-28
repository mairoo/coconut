package kr.co.pincoin.api.app.catalog.member.controller

import kr.co.pincoin.api.app.catalog.member.request.CategorySearchRequest
import kr.co.pincoin.api.app.catalog.member.service.MemberCategoryService
import kr.co.pincoin.api.app.catalog.member.response.CategoryResponse
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/categories")
class MemberCategoryController(
    private val memberCategoryService: MemberCategoryService,
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
        memberCategoryService.getCategories(request, pageable)
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
        memberCategoryService.getCategory(id, request)
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
        memberCategoryService.getCategory(slug, request)
            .let { CategoryResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}