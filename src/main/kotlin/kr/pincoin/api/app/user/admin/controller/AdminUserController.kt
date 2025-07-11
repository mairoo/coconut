package kr.pincoin.api.app.user.admin.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.admin.request.AdminUserCreateRequest
import kr.pincoin.api.app.user.admin.response.AdminUserResponse
import kr.pincoin.api.app.user.admin.service.AdminUserService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/admin/users")
class AdminUserController(
    private val adminUserService: AdminUserService,
) {
    @PostMapping
    fun createUser(
        @Valid @RequestBody request: AdminUserCreateRequest,
    ): ResponseEntity<ApiResponse<AdminUserResponse>> =
        adminUserService.createUser(request)
            .let { AdminUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}