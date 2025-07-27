package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.request.SignUpRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.app.auth.response.SignUpCompletedResponse
import kr.pincoin.api.app.auth.response.SignUpRequestedResponse
import kr.pincoin.api.app.auth.service.AuthService
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import kr.pincoin.api.global.constant.CookieKey
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val keycloakProperties: KeycloakProperties,
) {
    // 1-.1 회원 가입 폼 처리
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

    // 2. 로그인
    @PostMapping("/sign-in")
    fun signIn(
        @Valid @RequestBody request: SignInRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val signInResult = authService.signIn(request, httpServletRequest)

        // 리프레시 토큰을 HTTP-only 쿠키로 설정
        val headers = createRefreshTokenCookie(
            refreshToken = signInResult.refreshToken,
            request = httpServletRequest,
            rememberMe = request.rememberMe,
            refreshExpiresIn = signInResult.refreshExpiresIn,
        )

        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.of(signInResult.accessTokenResponse))
    }

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
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME) refreshToken: String,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val signInResult = authService.rotate(refreshToken, httpServletRequest)

        // 리프레시 토큰을 HTTP-only 쿠키로 설정
        val headers = createRefreshTokenCookie(
            refreshToken = signInResult.refreshToken,
            request = httpServletRequest,
            rememberMe = true,
            refreshExpiresIn = signInResult.refreshExpiresIn,
        )

        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.of(signInResult.accessTokenResponse))
    }

    // 4. 로그아웃
    /**
     * 사용자 로그아웃
     *
     * - Keycloak 세션 무효화
     * - HTTP-only 쿠키 삭제 (리프레시 토큰)
     *
     * @return 로그아웃 성공 응답
     */
    @PostMapping("/sign-out")
    fun signOut(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME, required = false) refreshToken: String?,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<String>> {
        // 리프레시 토큰이 있는 경우에만 Keycloak 로그아웃 처리
        if (!refreshToken.isNullOrBlank()) {
            authService.signOut(refreshToken, httpServletRequest)
        }

        // 쿠키 삭제를 위한 헤더 생성 (토큰 값을 null로 설정)
        val headers = createRefreshTokenCookie(
            refreshToken = null,
            request = httpServletRequest,
        )

        return ResponseEntity.ok()
            .headers(headers)
            .body(ApiResponse.of("로그아웃이 완료되었습니다."))
    }

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