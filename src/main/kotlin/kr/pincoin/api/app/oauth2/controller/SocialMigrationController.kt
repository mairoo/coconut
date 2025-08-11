package kr.pincoin.api.app.oauth2.controller

import kr.pincoin.api.app.oauth2.response.SocialMigrationResponse
import kr.pincoin.api.app.oauth2.service.SocialMigrationService
import kr.pincoin.api.global.response.success.ApiResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.jwt.Jwt
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/oauth2")
class SocialMigrationController(
    private val socialMigrationService: SocialMigrationService,
) {
    /**
     * 소셜 로그인 마이그레이션 처리
     *
     * **사용 시나리오:**
     * - Next.js + NextAuth.js에서 Keycloak 로그인 완료 후 호출
     * - NextAuth가 받은 JWT 토큰으로 Spring Boot API에서 마이그레이션 처리
     *
     * **인증 방식:**
     * - Bearer JWT 토큰 (NextAuth에서 받은 Keycloak access_token)
     * - Spring Security OAuth2 Resource Server로 토큰 검증
     *
     * **주요 작업:**
     * 1. JWT 토큰에서 사용자 정보 추출
     * 2. 기존 레거시 사용자와 Keycloak 계정 연동
     * 3. 마이그레이션 상태 반환
     */
    @PostMapping("/migrate")
    fun handleSocialMigration(
        @AuthenticationPrincipal jwt: Jwt,
    ): ResponseEntity<ApiResponse<SocialMigrationResponse>> =
        socialMigrationService.handleUserMigrationFromJwt(jwt)
            .let { ApiResponse.of(it) }
            .let { ResponseEntity.ok(it) }
}