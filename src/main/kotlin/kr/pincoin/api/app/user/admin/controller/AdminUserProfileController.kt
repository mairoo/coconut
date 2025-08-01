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
    @PutMapping("/{userId}/password")
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

    /**
     * 임시 비밀번호 설정 (다음 로그인 시 변경 강제)
     */
    @PutMapping("/{userId}/password/temporary")
    fun setTemporaryPassword(
        @PathVariable userId: Int,
        @Valid @RequestBody request: AdminPasswordChangeRequest,
    ): ResponseEntity<ApiResponse<Nothing?>> =
        adminUserProfileService.setTemporaryPassword(userId, request.newPassword)
            .let {
                ApiResponse.of(
                    data = null,
                    message = "임시 비밀번호가 설정되었습니다. 사용자는 다음 로그인 시 비밀번호를 변경해야 합니다",
                )
            }
            .let { ResponseEntity.ok(it) }
}