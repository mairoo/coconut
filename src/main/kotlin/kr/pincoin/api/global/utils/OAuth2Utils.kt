package kr.pincoin.api.global.utils

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.global.exception.BusinessException
import java.net.URI
import java.net.URLEncoder
import java.nio.charset.StandardCharsets
import java.security.SecureRandom
import java.util.*

/**
 * OAuth2 관련 URL/URI 처리 유틸리티
 */
object OAuth2Utils {
    private val logger = KotlinLogging.logger {}
    private val secureRandom = SecureRandom()

    /**
     * OAuth2 Authorization URL 구성
     *
     * @param baseUrl Keycloak authorization 엔드포인트 기본 URL
     * @param clientId OAuth2 클라이언트 ID
     * @param redirectUri 콜백 URI
     * @param state CSRF 방어용 상태값
     * @param scope 요청할 스코프 (기본값: "openid profile email")
     * @return 완성된 authorization URL
     */
    fun buildAuthorizationUrl(
        baseUrl: String,
        clientId: String,
        redirectUri: String,
        state: String,
        scope: String = "openid profile email",
    ): String {
        val params = mapOf(
            "response_type" to "code",
            "client_id" to clientId,
            "redirect_uri" to redirectUri,
            "scope" to scope,
            "state" to state,
        )

        val queryString = params.map { (key, value) ->
            "${urlEncode(key)}=${urlEncode(value)}"
        }.joinToString("&")

        return "$baseUrl?$queryString"
    }

    /**
     * 보안 state 값 생성
     *
     * CSRF 공격을 방어하기 위한 랜덤 state 값을 생성합니다.
     *
     * @param lengthBytes 랜덤 바이트 길이 (기본값: 24바이트)
     * @return URL-safe Base64 인코딩된 랜덤 문자열
     */
    fun generateSecureState(lengthBytes: Int = 24): String {
        val randomBytes = ByteArray(lengthBytes)
        secureRandom.nextBytes(randomBytes)
        return Base64.getUrlEncoder().withoutPadding().encodeToString(randomBytes)
    }

    /**
     * Redirect URI 보안 검증
     *
     * @param redirectUri 검증할 redirect URI
     * @param allowedUris 허용된 URI 목록
     * @param clientInfo 클라이언트 정보 (로깅용)
     * @throws BusinessException 유효하지 않은 URI인 경우
     */
    fun validateRedirectUri(
        redirectUri: String,
        allowedUris: List<String>,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        try {
            val uri = URI(redirectUri)

            // 스키마 검증
            validateUriScheme(uri, redirectUri, clientInfo)

            // 호스트 검증
            validateUriHost(uri, redirectUri, clientInfo)

            // 허용된 URI 목록과 매칭
            validateAllowedUris(redirectUri, allowedUris, clientInfo)

        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.warn {
                "잘못된 형식의 redirect_uri: $redirectUri, ip=${clientInfo.ipAddress}, " +
                        "userAgent=${clientInfo.userAgent}, error=${e.message}"
            }
            throw BusinessException(UserErrorCode.INVALID_REDIRECT_URI)
        }
    }

    /**
     * URI 스키마 검증
     */
    private fun validateUriScheme(
        uri: URI,
        redirectUri: String,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        when (uri.scheme?.lowercase()) {
            "http" -> {
                // HTTP는 localhost에서만 허용 (개발환경)
                if (!isLocalhost(uri.host)) {
                    logger.warn {
                        "HTTP redirect_uri는 localhost에서만 허용: " +
                                "uri=$redirectUri, ip=${clientInfo.ipAddress}, userAgent=${clientInfo.userAgent}"
                    }
                    throw BusinessException(UserErrorCode.INVALID_REDIRECT_URI)
                }
            }

            "https" -> {
                // HTTPS는 항상 허용
            }

            else -> {
                logger.warn {
                    "지원하지 않는 스키마: scheme=${uri.scheme}, uri=$redirectUri, " +
                            "ip=${clientInfo.ipAddress}, userAgent=${clientInfo.userAgent}"
                }
                throw BusinessException(UserErrorCode.INVALID_REDIRECT_URI)
            }
        }
    }

    /**
     * URI 호스트 검증
     */
    private fun validateUriHost(
        uri: URI,
        redirectUri: String,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        if (uri.host.isNullOrBlank()) {
            logger.warn { "호스트가 없는 redirect_uri: $redirectUri, ip=${clientInfo.ipAddress}" }
            throw BusinessException(UserErrorCode.INVALID_REDIRECT_URI)
        }
    }

    /**
     * 허용된 URI 목록 검증
     */
    private fun validateAllowedUris(
        redirectUri: String,
        allowedUris: List<String>,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        if (!isAllowedRedirectUri(redirectUri, allowedUris)) {
            logger.warn {
                "허용되지 않은 redirect_uri: uri=$redirectUri, ip=${clientInfo.ipAddress}, " +
                        "userAgent=${clientInfo.userAgent}, allowedUris=$allowedUris"
            }
            throw BusinessException(UserErrorCode.INVALID_REDIRECT_URI)
        }
    }

    /**
     * redirect URI가 허용된 목록에 있는지 검증
     */
    fun isAllowedRedirectUri(
        redirectUri: String,
        allowedUris: List<String>,
    ): Boolean {
        // 정확한 매칭
        if (redirectUri in allowedUris) return true

        // 패턴 매칭
        return allowedUris.any { pattern ->
            when {
                pattern.contains("*") -> {
                    // 와일드카드를 정규표현식으로 변환
                    val regex = pattern
                        .replace(".", "\\.")  // 점을 리터럴로 처리
                        .replace("*", "[a-zA-Z0-9-]+")  // 와일드카드를 영숫자+하이픈으로 매칭
                        .toRegex()
                    regex.matches(redirectUri)
                }

                pattern.endsWith("/*") -> {
                    // 경로 와일드카드 (/auth/* 같은 패턴)
                    redirectUri.startsWith(pattern.dropLast(1))
                }

                else -> false
            }
        }
    }

    /**
     * localhost 호스트 확인
     */
    fun isLocalhost(host: String?): Boolean {
        if (host == null) return false
        return host in listOf("localhost", "127.0.0.1", "::1") ||
                host.startsWith("192.168.") ||
                host.startsWith("10.") ||
                host.startsWith("172.")
    }

    /**
     * URL 인코딩 헬퍼 메서드
     */
    fun urlEncode(value: String): String =
        URLEncoder.encode(value, StandardCharsets.UTF_8)

    /**
     * Authorization URL에서 파라미터 추출
     *
     * @param url Authorization URL
     * @return 파라미터 맵
     */
    fun extractUrlParameters(url: String): Map<String, String> {
        return try {
            val uri = URI(url)
            val query = uri.query ?: return emptyMap()

            query.split("&")
                .mapNotNull { param ->
                    val parts = param.split("=", limit = 2)
                    if (parts.size == 2) {
                        parts[0] to parts[1]
                    } else {
                        null
                    }
                }
                .toMap()
        } catch (e: Exception) {
            logger.warn { "URL 파라미터 추출 실패: $url - ${e.message}" }
            emptyMap()
        }
    }

    /**
     * State 값 검증
     *
     * @param receivedState 받은 state 값
     * @param expectedState 예상되는 state 값
     * @param clientInfo 클라이언트 정보 (로깅용)
     * @throws BusinessException state 불일치 시
     */
    fun validateState(
        receivedState: String?,
        expectedState: String,
        clientInfo: ClientUtils.ClientInfo,
    ) {
        if (receivedState != expectedState) {
            logger.warn {
                "State 불일치 감지: received=$receivedState, expected=$expectedState, " +
                        "ip=${clientInfo.ipAddress}, userAgent=${clientInfo.userAgent}"
            }
            throw BusinessException(UserErrorCode.INVALID_STATE_PARAMETER)
        }
    }
}