package kr.pincoin.api.app.user.admin.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.app.user.admin.request.AdminUserSearchRequest
import kr.pincoin.api.app.user.admin.response.AdminUserProfileResponse
import kr.pincoin.api.app.user.admin.response.AdminUserResponse
import kr.pincoin.api.app.user.admin.service.AdminUserService
import kr.pincoin.api.global.response.page.PageResponse
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.domain.Pageable
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService,
) {
    @GetMapping
    fun searchUsersWithProfile(
        request: AdminUserSearchRequest,
        pageable: Pageable,
    ): ResponseEntity<ApiResponse<PageResponse<AdminUserProfileResponse>>> =
        adminUserService.findUsersWithProfile(request.toSearchCriteria(), pageable)
            .map(AdminUserProfileResponse::from)
            .let { PageResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/{userId}")
    fun getUserWithProfile(
        @PathVariable userId: Int,
    ): ResponseEntity<ApiResponse<AdminUserProfileResponse>> =
        adminUserService.findUserWithProfile(
            userId = userId,
            UserSearchCriteria(userId = userId.toLong())
        )
            .let { AdminUserProfileResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 단일 식별자로 사용자와 프로필을 검색하여 정확히 일치하는 한 건을 반환
     */
    @GetMapping("/unique")
    fun getUserWithProfile(
        request: AdminUserSearchRequest,
    ): ResponseEntity<ApiResponse<AdminUserProfileResponse>> {
        val identifierConditions = listOfNotNull(
            request.username,
            request.email
        )

        require(identifierConditions.size == 1) { "검색 조건(username, email)은 하나만 지정해야 합니다." }

        return adminUserService.findUserWithProfile(request.toSearchCriteria())
            .let { AdminUserProfileResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
    }

    @PostMapping
    fun createUserWithProfile(
        @Valid @RequestBody request: AdminUserCreateRequest,
    ): ResponseEntity<ApiResponse<AdminUserResponse>> =
        adminUserService.createUser(request)
            .let { AdminUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/{userId}")
    fun removeUser(
        @PathVariable userId: Int,
    ): ResponseEntity<ApiResponse<Unit>> =
        adminUserService.softDeleteUser(userId)
            .let { ApiResponse.of(data = Unit) }
            .let { ResponseEntity.ok(it) }
}