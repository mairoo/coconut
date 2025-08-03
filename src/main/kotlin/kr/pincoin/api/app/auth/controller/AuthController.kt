package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.MigrationRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.MigrationResponse
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.app.auth.service.AuthService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
) {
    // 1-1. 회원 가입 폼 처리
    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<SignUpRequestedResponse>> =
        authService.signUp(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    // 1-2. 이메일 인증 완료 시 회원 가입 완료 처리
    @GetMapping("/verify-email/{token}")
    fun verifyEmailAndCompleteSignup(
        @PathVariable token: String
    ): ResponseEntity<ApiResponse<SignUpCompletedResponse>> =
        authService.verifyEmailAndCompleteSignup(token)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    // 2. 레거시 사용자 마이그레이션
    @PostMapping("/migrate")
    fun migrateUser(
        @Valid @RequestBody request: MigrationRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<MigrationResponse>> =
        authService.migrateUser(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}