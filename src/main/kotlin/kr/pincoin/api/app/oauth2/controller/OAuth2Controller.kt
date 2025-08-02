package kr.pincoin.api.app.oauth2.controller

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.oauth2.request.OAuth2LoginUrlRequest
import kr.pincoin.api.app.oauth2.response.OAuth2LoginUrlResponse
import kr.pincoin.api.app.oauth2.service.OAuth2Service
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * OAuth2 Authorization Code Flow 인증 컨트롤러
 *
 * Keycloak의 OAuth2 로그인 URL을 생성하는 엔드포인트를 제공합니다.
 */
@RestController
@RequestMapping("/oauth2")
class OAuth2Controller(
    private val oauth2Service: OAuth2Service,
) {
    /**
     * OAuth2 로그인 URL 생성
     *
     * 클라이언트가 사용자를 Keycloak 로그인 페이지로 리다이렉트할 수 있는
     * authorization URL을 생성하여 반환
     */
    @GetMapping("/login-url")
    fun getOAuth2LoginUrl(
        request: OAuth2LoginUrlRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<OAuth2LoginUrlResponse>> =
        oauth2Service.generateLoginUrl(request.redirectUri, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}