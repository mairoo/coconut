package kr.pincoin.api.app.user.admin.controller

import jakarta.validation.Valid
import kr.pincoin.api.app.user.admin.request.AdminPasswordChangeRequest
import kr.pincoin.api.app.user.admin.service.AdminUserProfileService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/users")
class AdminUserProfileController(
    private val adminUserProfileService: AdminUserProfileService,
) {
    /**
     * 관리자가 사용자 비밀번호 강제 변경
     */
    @PatchMapping("/{userId}/password")
    fun changeUserPassword(
        @PathVariable userId: Int,
        @Valid @RequestBody request: AdminPasswordChangeRequest,
    ): ResponseEntity<ApiResponse<Nothing?>> =
        adminUserProfileService.changeUserPassword(userId, request)
            .let {
                ApiResponse.of(
                    data = null,
                    message = "사용자 비밀번호가 성공적으로 변경되었습니다",
                )
            }
            .let { ResponseEntity.ok(it) }
}