package kr.pincoin.api.global.properties

import kr.pincoin.api.global.utils.DomainUtils
import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "jwt")
data class JwtProperties(
    val secret: String,
    val accessTokenExpiresIn: Int,
    val refreshTokenExpiresIn: Long,
    val cookieDomains: List<String>,
    val oauth2RedirectUrl: String
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