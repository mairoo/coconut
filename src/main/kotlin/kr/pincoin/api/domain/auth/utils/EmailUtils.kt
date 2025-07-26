package kr.pincoin.api.domain.auth.utils

import kr.pincoin.api.domain.auth.properties.AuthProperties
import org.springframework.stereotype.Component

@Component
class EmailUtils(
    private val authProperties: AuthProperties,
) {
    /**
     * 이메일 마스킹 처리
     * 예: "user@example.com" -> "u***@example.com"
     */
    fun maskEmail(email: String): String {
        val parts = email.split("@")
        if (parts.size != 2) return email

        val localPart = parts[0]
        val domain = parts[1]

        val maskedLocal = when {
            localPart.length <= 2 -> localPart
            localPart.length <= 4 -> "${localPart.first()}***"
            else -> "${localPart.substring(0, 2)}***${localPart.last()}"
        }

        return "$maskedLocal@$domain"
    }

    /**
     * 이메일 도메인 검증
     */
    fun isAllowedDomain(
        email: String,
    ): Boolean =
        authProperties.email.isEmailAllowed(email)

    /**
     * 특정 도메인이 허용되는지 확인
     */
    fun isDomainAllowed(
        domain: String,
    ): Boolean =
        authProperties.email.isDomainAllowed(domain)

    /**
     * 이메일이 차단된 도메인인지 확인
     */
    fun isBlockedDomain(
        email: String,
    ): Boolean =
        authProperties.email.blockedDomains.contains(email.substringAfterLast("@", "").lowercase())

    /**
     * 허용된 도메인 목록 반환
     */
    fun getAllowedDomains(): List<String> =
        authProperties.email.allowedDomains

    /**
     * 차단된 도메인 목록 반환
     */
    fun getBlockedDomains(): List<String> =
        authProperties.email.blockedDomains
}