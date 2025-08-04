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
    @GetMapping("/users/{targetUserEmail}/status")
    suspend fun getUserTotpStatus(
        @PathVariable targetUserEmail: String
    ): ResponseEntity<ApiResponse<TotpStatusResponse>> =
        adminTotpService.getTotpStatus(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @PostMapping("/users/{targetUserEmail}/force-setup")
    suspend fun forceUserTotpSetup(
        @PathVariable targetUserEmail: String,
    ): ResponseEntity<ApiResponse<String>> =
        adminTotpService.forceUserTotpSetup(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @DeleteMapping("/users/{targetUserEmail}/force-disable")
    suspend fun forceDisableUserTotp(
        @PathVariable targetUserEmail: String,
    ): ResponseEntity<ApiResponse<String>> =
        adminTotpService.disableTotp(targetUserEmail)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}