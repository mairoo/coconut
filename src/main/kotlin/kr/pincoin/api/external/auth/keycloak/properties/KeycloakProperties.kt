package kr.pincoin.api.external.auth.keycloak.properties

import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "keycloak")
data class KeycloakProperties(
    val realm: String = "zerocan",

    val clientId: String = "zerocan-backend",

    val clientSecret: String = "",

    val serverUrl: String = "http://localhost:8081",

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
) {
    fun findCookieDomain(requestDomain: String): String {
        // 도메인에서 포트 제거
        val domainWithoutPort = DomainUtils.stripPort(requestDomain)

        // 1. 정확히 일치하는 도메인 찾기
        cookieDomains.find { it == domainWithoutPort }?.let {
            return it
        }

        // 2. 와일드카드 도메인 찾기 (.으로 시작하는 도메인)
        cookieDomains.filter { it.startsWith(".") }
            .find { domainWithoutPort.endsWith(it.substring(1)) }?.let {
                return it
            }

        // 3. 일치하는 것이 없으면 요청 도메인 그대로 사용
        return domainWithoutPort
    }
}