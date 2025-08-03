package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.MigrationRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.MigrationResponse
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.app.auth.service.MigrationFacade
import kr.pincoin.api.app.auth.service.SignUpFacade
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val signUpFacade: SignUpFacade,
    private val migrationFacade: MigrationFacade,
) {
    /**
     * 1-1. 회원가입 요청 처리
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
     * 1-2. 이메일 인증 완료 및 회원가입 완료 처리
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

    /**
     * 2. 레거시 사용자 마이그레이션
     *
     * Django allauth 기반 레거시 사용자를 Keycloak으로 마이그레이션합니다.
     *
     * **처리 단계:**
     * 1. 보안 검증 (reCAPTCHA 등)
     * 2. 레거시 사용자 검증 (PBKDF2 비밀번호 확인)
     * 3. 이미 마이그레이션된 사용자 확인
     * 4. Keycloak으로 마이그레이션 수행
     *
     * **마이그레이션 프로세스:**
     * - **사용자 확인**: 기존 User 테이블에서 사용자 조회
     * - **비밀번호 검증**: 레거시 패스워드 인코더(PBKDF2)로 비밀번호 검증
     * - **중복 확인**: 이미 keycloak_id가 있는 사용자는 마이그레이션 완료로 처리
     * - **Keycloak 생성**: 레거시 사용자를 Keycloak에 생성
     * - **연결**: User 테이블의 keycloak_id 업데이트
     *
     * **보안 기능:**
     * - reCAPTCHA 검증 (무작위 공격 방어)
     * - 향후 확장: IP별 마이그레이션 제한 등
     */
    @PostMapping("/migrate")
    fun migrateUser(
        @Valid @RequestBody request: MigrationRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<MigrationResponse>> =
        migrationFacade.processMigration(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}