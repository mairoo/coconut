package kr.pincoin.api.app.auth.response

/**
 * 서블릿 요청에서 수집된 다양한 클라이언트 및 서버 정보를 담는 응답 객체
 */
data class RequestInfoResponse(
    val headers: Map<String, String>,
    val serverInfo: ServerInfo,
    val clientInfo: ClientInfo,
    val cookieInfo: Map<String, String>,
    val jwtInfo: JwtInfo,
) {
    /**
     * 서버 관련 정보
     */
    data class ServerInfo(
        val serverName: String,
        val serverPort: Int,
        val scheme: String,
        val requestURI: String,
        val requestURL: String,
        val contextPath: String,
        val servletPath: String,
        val queryString: String?,
        val calculatedCookieDomain: String,
    )

    /**
     * 클라이언트 관련 정보
     */
    data class ClientInfo(
        val remoteAddr: String,
        val remoteHost: String,
        val remotePort: Int,
        val resolvedClientIp: String,
        val userAgent: String,
        val acceptLanguage: String,
    )

    /**
     * JWT 관련 정보
     */
    data class JwtInfo(
        val cookieDomains: List<String>,
    )
}