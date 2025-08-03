package kr.pincoin.api.external.auth.keycloak.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    val realm: String = "zerocan",

    val clientId: String = "zerocan-backend",

    val clientSecret: String = "",

    val internalUrl: String = "http://keycloak:8080",

    val publicUrl: String = "http://localhost:8081",

    val timeout: Long = 10000,

    val cookieDomains: List<String> = emptyList(),

    /**
     * OAuth2 Authorization Code Flow에서 허용되는 redirect URI 목록
     *
     * 보안을 위해 사전 정의된 URI만 허용합니다.
     * 와일드카드(*) 패턴을 지원하여 유연한 설정이 가능합니다.
     */

    // 예시
    // "http://localhost:3000/auth/callback" - 정확한 매칭
    // "http://localhost:*/auth/callback" - 포트 와일드카드
    // "https://*.yourdomain.com/auth/callback" - 서브도메인 와일드카드
    // "https://yourdomain.com/auth/*" - 경로 와일드카드

    val allowedRedirectUris: List<String> = emptyList(),
)