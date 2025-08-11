package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.app.auth.service.SignUpFacade
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val signUpFacade: SignUpFacade,

    ) {
    /**
     * 1. 회원가입 요청 처리
     *
     * 이메일 인증이 필요한 2단계 회원가입의 첫 번째 단계입니다.
     * - 입력값 검증 (reCAPTCHA, 이메일 도메인, IP 제한 등)
     * - 이메일 중복 검증 (사전 차단)
     * - 인증 이메일 발송
     * - 임시 데이터 저장
     */
    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<SignUpRequestedResponse>> =
        signUpFacade.processSignUpRequest(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 2. 이메일 인증 완료 및 회원가입 완료 처리
     *
     * 이메일 인증이 필요한 2단계 회원가입의 두 번째 단계입니다.
     * - 인증 토큰 검증
     * - 이메일 중복 재검증 (방어적 프로그래밍)
     * - Keycloak 사용자 생성
     * - 데이터베이스 사용자 정보 저장
     * - 임시 데이터 정리
     * - 환영 이메일 발송
     */
    @GetMapping("/verify-email/{token}")
    fun verifyEmailAndCompleteSignup(
        @PathVariable token: String
    ): ResponseEntity<ApiResponse<SignUpCompletedResponse>> =
        signUpFacade.completeSignUp(token)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}