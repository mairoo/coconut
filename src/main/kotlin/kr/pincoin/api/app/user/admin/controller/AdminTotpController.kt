package kr.pincoin.api.app.user.admin.controller

import kr.pincoin.api.app.user.admin.service.AdminTotpService
import kr.pincoin.api.app.user.common.response.TotpStatusResponse
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/admin/totp")
class AdminTotpController(
    private val adminTotpService: AdminTotpService,
) {
    /**
     * 특정 사용자의 2FA 상태 조회
     */
    @GetMapping("/users/{targetUserEmail}/status")
    fun getUserTotpStatus(
        @PathVariable targetUserEmail: String
    ): ResponseEntity<ApiResponse<TotpStatusResponse>> =
        adminTotpService.getTotpStatus(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 특정 사용자에게 2FA 설정 강제
     */
    @PostMapping("/users/{targetUserEmail}/force-setup")
    fun forceUserTotpSetup(
        @PathVariable targetUserEmail: String,
    ): ResponseEntity<ApiResponse<String>> =
        adminTotpService.forceUserTotpSetup(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 특정 사용자의 2FA 강제 비활성화
     */
    @DeleteMapping("/users/{targetUserEmail}/force-disable")
    fun forceDisableUserTotp(
        @PathVariable targetUserEmail: String,
    ): ResponseEntity<ApiResponse<String>> =
        adminTotpService.disableTotp(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}