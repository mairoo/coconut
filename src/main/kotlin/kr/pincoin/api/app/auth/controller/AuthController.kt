package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.service.AuthService
import kr.pincoin.api.app.user.common.response.UserResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.global.constant.CookieKey
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController(
    private val authService: AuthService,
    private val keycloakProperties: KeycloakProperties,
) {
    /**
     * 1-.1 회원 가입 폼 처리
     */
    @PostMapping("/sign-up")
    fun signUp(
        @Valid @RequestBody request: SignUpRequest,
    ): ResponseEntity<ApiResponse<UserResponse>> =
        authService.createUser(request)
            .let { UserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    // 1-2. 이메일 인증 완료 시 회원 가입 완료 처리
    // GET /auth/verify-email/{token}

    // 2. 로그인
    /**
     * 사용자 로그인 (Keycloak 우선, 레거시 마이그레이션 지원)
     *
     * **1단계: Keycloak 우선 인증 시도**
     * - 사용자 이메일/패스워드로 Keycloak 인증 요청
     * - 성공 시: JWT 액세스 토큰과 리프레시 토큰 반환
     * - 실패 시: 2단계로 진행
     *
     * **2단계: 레거시 사용자 검증**
     * - 기존 User 테이블에서 이메일로 사용자 조회
     * - 레거시 패스워드 인코더(PBKDF2)로 비밀번호 검증
     * - 검증 실패 시: 인증 오류 반환
     * - 검증 성공 시: 3단계로 진행
     *
     * **3단계: Keycloak 마이그레이션**
     * - 해당 사용자를 Keycloak에 새로 생성 (입력받은 패스워드로)
     * - User 테이블의 `keycloak_id` 필드 업데이트
     * - 새로 생성된 Keycloak 계정으로 JWT 토큰 발급
     *
     * **토큰 응답 형식**
     * - 액세스 토큰: 응답 본문으로 전달
     * - 리프레시 토큰: HTTP-only, Secure, SameSite=Strict 쿠키로 관리
     * - CORS 설정에서 credentials 허용 필요
     *
     * **이메일/비밀번호 무작위 로그인 공격 대응**
     * - Google reCAPTCHA 검증
     * - 2FA Google OTP 지원
     * - 계정 잠금 정책: 연속 실패 시 임시 잠금 (5회 실패 → 15분 잠금)
     * - 진행형 지연: 실패할수록 응답 시간 증가 (1초 → 2초 → 4초...)
     * - 디바이스 핑거프린팅: 알려지지 않은 디바이스에서의 접근 감지
     *
     * **동시성 처리**
     * - 마이그레이션 중 동시 로그인: DB 락 또는 재시도 로직으로 처리
     *
     * @param loginRequest 로그인 요청 정보 (email, password, recaptchaToken, otpCode?)
     * @return JWT 토큰 응답 (액세스 토큰 + HTTP-only 쿠키로 리프레시 토큰)
     */
    // POST /auth/login

    // 3. 리프레시
    /**
     * JWT 액세스 토큰 갱신
     *
     * - Keycloak의 리프레시 토큰 엔드포인트 호출
     * - 새로운 액세스 토큰 발급
     * - 리프레시 토큰은 HTTP-only, Secure, SameSite=Strict 쿠키로 관리
     * - CORS 설정에서 credentials 허용 필요
     *
     * @return 새로운 액세스 토큰 응답 (응답 본문)
     */
    // POST /auth/refresh

    // 4. 로그아웃
    /**
     * 사용자 로그아웃
     *
     * - Keycloak 세션 무효화
     * - HTTP-only 쿠키 삭제 (리프레시 토큰)
     *
     * @return 로그아웃 성공 응답
     */
    // POST /auth/logout

    /**
     * 리프레시 토큰을 포함하는 HTTP 쿠키 생성
     */
    private fun createRefreshTokenCookie(
        refreshToken: String?,
        request: HttpServletRequest,
        rememberMe: Boolean = false,
        refreshExpiresIn: Long? = null,
    ): HttpHeaders =
        HttpHeaders().apply {
            val cookieValue = refreshToken?.takeIf { it.isNotEmpty() }
            val requestDomain = DomainUtils.getRequestDomain(request)
            val cookieDomain = keycloakProperties.findCookieDomain(requestDomain)

            val cookie = ResponseCookie.from(CookieKey.REFRESH_TOKEN_NAME, cookieValue ?: "")
                .httpOnly(true)
                .secure(!requestDomain.contains("localhost"))
                .path(CookieKey.PATH)
                .maxAge(
                    when { // rememberMe와 실제 토큰 만료시간에 따른 쿠키 만료시간 설정
                        cookieValue == null -> 0 // 쿠키 삭제
                        rememberMe -> requireNotNull(refreshExpiresIn) { "refreshExpiresIn == null" }
                        else -> -1 // rememberMe=false: 세션 쿠키 (브라우저 종료시 삭제)
                    }
                )
                .sameSite(CookieKey.SAME_SITE)
                .domain(cookieDomain)
                .build()

            add(HttpHeaders.SET_COOKIE, cookie.toString())

            // 인증 API 보안 헤더
            add("X-Content-Type-Options", "nosniff") // MIME 스니핑 공격 방어
            add("X-Frame-Options", "DENY") // 클릭재킹 공격 방어

            // HTTPS 환경에서만 추가 보안 헤더 적용 (1년간 유효)
            if (request.isSecure) {
                add("Strict-Transport-Security", "max-age=31536000") // HTTPS 강제
            }
        }
}