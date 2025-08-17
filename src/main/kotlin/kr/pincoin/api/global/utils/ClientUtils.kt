package kr.pincoin.api.global.utils

import jakarta.servlet.http.HttpServletRequest

object ClientUtils {
    private const val USER_AGENT_HEADER = "User-Agent"
    private const val ACCEPT_LANGUAGE_HEADER = "Accept-Language"

    /**
     * HttpServletRequest에서 클라이언트 정보를 추출
     */
    fun getClientInfo(request: HttpServletRequest): ClientInfo =
        ClientInfo(
            userAgent = request.getHeader(USER_AGENT_HEADER).orEmpty(),
            acceptLanguage = request.getHeader(ACCEPT_LANGUAGE_HEADER).orEmpty(),
            ipAddress = IpUtils.getClientIp(request),
            requestDomain = DomainUtils.getRequestDomain(request),
            isSecure = request.isSecure,
            serverPort = request.serverPort
        )

    /** 클라이언트 요청 정보를 담는 데이터 클래스 */
    data class ClientInfo(
        val userAgent: String,
        val acceptLanguage: String,
        val ipAddress: String,
        val requestDomain: String,
        val isSecure: Boolean,
        val serverPort: Int
    ) {
        /**
         * 완전한 기본 URL 생성
         */
        fun getBaseUrl(): String {
            val scheme = if (isSecure) "https" else "http"
            val domain = DomainUtils.stripPort(requestDomain)

            return when {
                // HTTPS 기본 포트(443) 또는 HTTP 기본 포트(80)인 경우 포트 생략
                (isSecure && serverPort == 443) || (!isSecure && serverPort == 80) -> "$scheme://$domain"
                // 기본 포트가 아닌 경우 포트 포함
                else -> "$scheme://$domain:$serverPort"
            }
        }
    }
}