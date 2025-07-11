package kr.pincoin.api.app.auth.controller

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.app.auth.response.AccessTokenResponse
import kr.pincoin.api.app.auth.response.RequestInfoResponse
import kr.pincoin.api.app.auth.service.AuthService
import kr.pincoin.api.app.user.member.request.MemberUserCreateRequest
import kr.pincoin.api.app.user.member.response.MemberUserResponse
import kr.pincoin.api.global.constant.CookieKey
import kr.pincoin.api.global.properties.JwtProperties
import kr.pincoin.api.global.response.success.ApiResponse
import kr.pincoin.api.global.utils.DomainUtils
import kr.pincoin.api.global.utils.IpUtils
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
            .headers(createRefreshTokenCookie(tokenPair.refreshToken, servletRequest))
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
            .headers(createRefreshTokenCookie(tokenPair.refreshToken, servletRequest))
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
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<Unit>> {
        refreshToken?.let { authService.logout(it) }

        return ResponseEntity.ok()
            .headers(createRefreshTokenCookie(null, servletRequest)) // 쿠키 삭제 효과
            .body(ApiResponse.of(Unit))
    }

    @PostMapping("/sign-up")
    fun createUser(
        @Valid @RequestBody request: MemberUserCreateRequest,
    ): ResponseEntity<ApiResponse<MemberUserResponse>> =
        authService.createUser(request)
            .let { MemberUserResponse.from(it) }
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    @GetMapping("/echo")
    fun echo(
        servletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<RequestInfoResponse>> {
        // 헤더 정보 추출
        val headerNames = servletRequest.headerNames.toList()
        val headers = headerNames.associateWith { servletRequest.getHeader(it) }

        // 쿠키 정보 추출
        val cookies = servletRequest.cookies?.associate {
            it.name to it.value
        } ?: emptyMap()

        // 클라이언트 정보
        val clientInfo = RequestInfoResponse.ClientInfo(
            remoteAddr = servletRequest.remoteAddr,
            remoteHost = servletRequest.remoteHost,
            remotePort = servletRequest.remotePort,
            resolvedClientIp = IpUtils.getClientIp(servletRequest),
            userAgent = servletRequest.getHeader("User-Agent").orEmpty(),
            acceptLanguage = servletRequest.getHeader("Accept-Language").orEmpty(),
        )

        // 서버 정보
        val requestDomain = servletRequest.serverName
        val calculatedCookieDomain = jwtProperties.findCookieDomain(requestDomain)

        val serverInfo = RequestInfoResponse.ServerInfo(
            serverName = servletRequest.serverName,
            serverPort = servletRequest.serverPort,
            scheme = servletRequest.scheme,
            requestURI = servletRequest.requestURI,
            requestURL = servletRequest.requestURL.toString(),
            contextPath = servletRequest.contextPath,
            servletPath = servletRequest.servletPath,
            queryString = servletRequest.queryString,
            calculatedCookieDomain = calculatedCookieDomain,
        )

        // JWT 정보
        val jwtInfo = RequestInfoResponse.JwtInfo(
            cookieDomains = jwtProperties.cookieDomains,
        )

        val responseData = RequestInfoResponse(
            headers = headers,
            serverInfo = serverInfo,
            clientInfo = clientInfo,
            cookieInfo = cookies,
            jwtInfo = jwtInfo,
        )

        return ResponseEntity.ok()
            .body(ApiResponse.of(responseData))
    }

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