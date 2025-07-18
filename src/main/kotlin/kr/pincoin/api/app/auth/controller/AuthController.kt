package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.request.UserCreateRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.app.auth.service.JwtKeycloakAuthServiceImpl
import kr.pincoin.api.app.user.my.response.MyUserResponse
import kr.pincoin.api.global.constant.CookieKey
import kr.pincoin.api.global.properties.JwtProperties
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/auth")
class AuthController(
    private val jwtProperties: JwtProperties,
    private val authService: JwtKeycloakAuthServiceImpl,
) {
    /**
     * 사용자 로그인을 처리하고 액세스 토큰과 리프레시 토큰을 발급합니다.
     */
    @PostMapping("/login")
    fun signIn(
        @Valid @RequestBody request: SignInRequest,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val tokenPair = authService.login(request, servletRequest)

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(tokenPair.refreshToken, servletRequest))
            .body(ApiResponse.of(tokenPair.accessToken))
    }

    /**
     * 리프레시 토큰을 사용하여 새로운 액세스 토큰과 리프레시 토큰을 발급합니다.
     */
    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME) refreshToken: String,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<AccessTokenResponse>> {
        val tokenPair = authService.refresh(refreshToken, servletRequest)

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(tokenPair.refreshToken, servletRequest))
            .body(ApiResponse.of(tokenPair.accessToken))
    }

    /**
     * 사용자 로그아웃을 처리하고 리프레시 토큰을 무효화합니다.
     */
    @PostMapping("/logout")
    fun signOut(
        @CookieValue(name = CookieKey.REFRESH_TOKEN_NAME, required = false) refreshToken: String?,
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        refreshToken?.let { authService.logout(it) }

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(null, servletRequest)) // 쿠키 삭제 효과
            .body(ApiResponse.of(Unit))
    }

    /**
     * 회원 가입을 합니다.
     */
    @PostMapping("/register")
    fun signUp(
        @Valid @RequestBody request: UserCreateRequest,
    ): ResponseEntity<ApiResponse<MyUserResponse>> =
        authService.createUser(request)
            .let { MyUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * 리프레시 토큰을 포함하는 HTTP 쿠키를 생성합니다.
     */
    private fun createRefreshTokenCookie(
        refreshToken: String?,
        request: HttpServletRequest,
    ): HttpHeaders =
        HttpHeaders().apply {
            val cookieValue = refreshToken?.takeIf { it.isNotEmpty() }
            val requestDomain = DomainUtils.getRequestDomain(request)
            val cookieDomain = jwtProperties.findCookieDomain(requestDomain)

            val cookie = ResponseCookie.from(CookieKey.REFRESH_TOKEN_NAME, cookieValue ?: "")
                .httpOnly(true)
                .secure(!requestDomain.contains("localhost"))
                .path(CookieKey.PATH)
                .maxAge(cookieValue?.let { jwtProperties.refreshTokenExpiresIn } ?: 0)
                .sameSite(CookieKey.SAME_SITE)
                .domain(cookieDomain)
                .build()

            add(HttpHeaders.SET_COOKIE, cookie.toString())
        }
}