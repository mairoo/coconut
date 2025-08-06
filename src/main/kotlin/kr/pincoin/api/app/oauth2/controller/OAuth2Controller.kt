package kr.pincoin.api.app.oauth2.controller

import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.oauth2.request.OAuth2CallbackRequest
import kr.pincoin.api.app.oauth2.request.OAuth2LoginUrlRequest
import kr.pincoin.api.app.oauth2.response.OAuth2LoginUrlResponse
import kr.pincoin.api.app.oauth2.response.OAuth2TokenResponse
import kr.pincoin.api.app.oauth2.service.OAuth2Service
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/oauth2")
class OAuth2Controller(
    private val oauth2Service: OAuth2Service,
) {
    /**
     * OAuth2 로그인 URL 생성
     *
     * **주요 작업:**
     * - Keycloak Authorization Server의 로그인 URL 생성
     * - CSRF 방지를 위한 state 파라미터 생성 및 세션 저장
     * - PKCE(Proof Key for Code Exchange) code_challenge 생성
     * - 클라이언트 리다이렉션용 authorization URL 반환
     */
    @GetMapping("/login-url")
    fun getOAuth2LoginUrl(
        request: OAuth2LoginUrlRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<OAuth2LoginUrlResponse>> =
        oauth2Service.generateLoginUrl(request.redirectUri, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }

    /**
     * OAuth2 콜백 처리 및 토큰 교환
     *
     * **주요 작업:**
     * 1. **보안 검증**: Authorization Code, state 파라미터 검증
     * 2. **토큰 교환**: Authorization Code → Access Token + Refresh Token
     * 3. **사용자 정보 획득**: Access Token으로 Keycloak에서 사용자 프로필 조회
     * 4. **마이그레이션 처리**: 기존 레거시 사용자 계정을 Keycloak과 통합
     * 5. **토큰 응답**: 클라이언트에 JWT 토큰 및 마이그레이션 상태 반환
     */
    @PostMapping("/callback")
    fun handleOAuth2Callback(
        @RequestBody request: OAuth2CallbackRequest,
        httpServletRequest: HttpServletRequest,
    ): ResponseEntity<ApiResponse<OAuth2TokenResponse>> =
        oauth2Service.exchangeCodeForToken(request, httpServletRequest)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}