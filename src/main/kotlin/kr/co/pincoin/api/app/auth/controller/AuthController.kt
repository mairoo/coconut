package kr.co.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.co.pincoin.api.app.auth.request.SignInRequest
import kr.co.pincoin.api.app.auth.response.AccessTokenResponse
import kr.co.pincoin.api.app.auth.service.AuthService
import kr.co.pincoin.api.global.constant.CookieKey
import kr.co.pincoin.api.global.properties.JwtProperties
import kr.co.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtProperties: JwtProperties,
    private val authService: AuthService,
) {
    /**
     * 사용자 로그인을 처리하고 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param request 로그인 요청 정보를 담은 객체
     * @param servletRequest HTTP 요청 객체
     * @return 액세스 토큰이 포함된 API 응답과 리프레시 토큰이 포함된 쿠키가 설정된 ResponseEntity
     */
    @PostMapping("/sign-in")
    fun signIn(
        @Valid @RequestBody request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val tokenPair = authService.login(request, servletRequest)

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(tokenPair.refreshToken))
            .body(ApiResponse.of(tokenPair.accessToken))
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     *
     * @param refreshToken 쿠키에서 추출한 리프레시 토큰
     * @param servletRequest HTTP 요청 객체
     * @return ResponseEntity 객체로 래핑된 새로운 액세스 토큰과 쿠키에 설정된 새로운 리프레시 토큰
     */
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME) refreshToken: String,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val tokenPair = authService.refresh(refreshToken, servletRequest)

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(tokenPair.refreshToken))
            .body(ApiResponse.of(tokenPair.accessToken))
    }

    /**
     * 사용자 로그아웃을 처리하고 리프레시 토큰을 무효화합니다.
     *
     * @param refreshToken 쿠키에서 추출한 리프레시 토큰 (선택적)
     * @return ResponseEntity 객체와 리프레시 토큰 쿠키를 제거하는 응답
     */
    @PostMapping("/sign-out")
    fun signOut(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME, required = false) refreshToken: String?,
    ): ResponseEntity<ApiResponse<Unit>> {
        refreshToken?.let { authService.logout(it) }

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(null)) // 쿠키 삭제 효과
            .body(ApiResponse.of(Unit))
    }

    /**
     * 리프레시 토큰을 포함하는 HTTP 쿠키를 생성합니다.
     *
     * @param refreshToken 설정할 리프레시 토큰 값
     * @return 리프레시 토큰 쿠키가 포함된 HTTP 헤더
     */
    private fun createRefreshTokenCookie(refreshToken: String?): HttpHeaders =
        HttpHeaders().apply {
            val cookieValue = refreshToken?.takeIf { it.isNotEmpty() }

            val cookie = ResponseCookie.from(CookieKey.REFRESH_TOKEN_NAME, cookieValue ?: "")
                .httpOnly(true)
                .secure(true)
                .path(CookieKey.PATH)
                .maxAge(cookieValue?.let { jwtProperties.refreshTokenExpiresIn } ?: 0)
                .sameSite(CookieKey.SAME_SITE)
                .domain(jwtProperties.cookieDomain)
                .build()

            add(HttpHeaders.SET_COOKIE, cookie.toString())
        }
}