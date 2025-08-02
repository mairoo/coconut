package kr.pincoin.api.external.auth.keycloak.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.type.CollectionType
import kotlinx.coroutines.reactor.awaitSingle
import kr.pincoin.api.external.auth.keycloak.api.request.*
import kr.pincoin.api.external.auth.keycloak.api.response.*
import kr.pincoin.api.external.auth.keycloak.error.KeycloakApiErrorCode
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.WebClientResponseException
import org.springframework.web.reactive.function.client.awaitBodilessEntity
import org.springframework.web.reactive.function.client.awaitBody

@Component
class KeycloakApiClient(
    private val keycloakWebClient: WebClient,
    private val keycloakProperties: KeycloakProperties,
    private val objectMapper: ObjectMapper,
) {

    // ========================================
    // Admin API - 사용자 관리
    // ========================================

    /**
     * Admin API - 사용자 생성
     */
    suspend fun createUser(
        adminToken: String,
        request: KeycloakCreateUserRequest,
    ): KeycloakResponse<KeycloakCreateUserData> =
        executePostWithLocation(
            uri = "/admin/realms/${keycloakProperties.realm}/users",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
            request = request,
        ) { locationHeader ->
            val userId = extractUserIdFromLocation(locationHeader)
            if (userId != null) {
                KeycloakResponse.Success(KeycloakCreateUserData(userId = userId))
            } else {
                KeycloakResponse.Error("PARSE_ERROR", "Location 헤더에서 사용자 ID를 추출할 수 없습니다")
            }
        }

    /**
     * Admin API - 사용자 정보 조회
     */
    suspend fun getUser(
        adminToken: String,
        userId: String,
    ): KeycloakResponse<KeycloakUserResponse> =
        executeGet(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
        ) { responseBody ->
            handleSuccessResponse(responseBody, KeycloakUserResponse::class.java)
        }

    /**
     * Admin API - 사용자 정보 수정
     */
    suspend fun updateUser(
        adminToken: String,
        userId: String,
        request: KeycloakUpdateUserRequest,
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executePutWithoutResponse(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
            request = request,
        )

    /**
     * Admin API - 사용자 삭제
     */
    suspend fun deleteUser(
        adminToken: String,
        userId: String,
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executeDeleteWithoutResponse(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
        )

    /**
     * Admin API - 사용자에게 필수 액션 설정
     */
    suspend fun setUserRequiredActions(
        adminToken: String,
        userId: String,
        requiredActions: List<String>
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executePutWithoutResponse(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
            request = KeycloakRequiredActionRequest(requiredActions = requiredActions),
        )

    // ========================================
    // Admin API - 인증정보 관리
    // ========================================

    /**
     * Admin API - 사용자의 인증정보 목록 조회
     */
    suspend fun getUserCredentials(
        adminToken: String,
        userId: String
    ): KeycloakResponse<List<KeycloakCredentialResponse>> =
        executeGet(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId/credentials",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken")
        ) { responseBody ->
            try {
                val collectionType: CollectionType = objectMapper.typeFactory
                    .constructCollectionType(List::class.java, KeycloakCredentialResponse::class.java)

                val credentials: List<KeycloakCredentialResponse> = objectMapper.readValue(responseBody, collectionType)
                KeycloakResponse.Success(credentials)
            } catch (e: Exception) {
                KeycloakResponse.Error("PARSE_ERROR", "인증정보 파싱 실패: ${e.message}")
            }
        }

    /**
     * Admin API - TOTP 인증정보 생성
     */
    suspend fun createTotpCredential(
        adminToken: String,
        userId: String,
        secret: String
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executePostWithoutResponse(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId/credentials",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
            request = KeycloakTotpSetupRequest(
                secretData = """{"value":"$secret"}""",
                credentialData = """{"subType":"totp","digits":6,"period":30,"algorithm":"HmacSHA1"}""",
            )
        )

    /**
     * Admin API - 특정 인증정보 삭제
     */
    suspend fun deleteCredential(
        adminToken: String,
        userId: String,
        credentialId: String
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executeDeleteWithoutResponse(
            uri = "/admin/realms/${keycloakProperties.realm}/users/$userId/credentials/$credentialId",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $adminToken"),
        )

    // ========================================
    // User Account API - 사용자 셀프 서비스
    // ========================================

    /**
     * User Account API - 사용자 비밀번호 변경
     */
    suspend fun changeUserPassword(
        accessToken: String,
        currentPassword: String,
        newPassword: String,
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executePutWithoutResponse(
            uri = "/realms/${keycloakProperties.realm}/account/credentials/password",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $accessToken"),
            request = mapOf(
                "currentPassword" to currentPassword,
                "newPassword" to newPassword,
                "confirmation" to newPassword,
            )
        )

    // ========================================
    // OpenID Connect API - 인증/토큰
    // ========================================

    /**
     * Admin 토큰 획득
     */
    suspend fun getAdminToken(
        request: KeycloakAdminTokenRequest,
    ): KeycloakResponse<KeycloakAdminTokenData> =
        when (val tokenResult = executeFormPost(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/token",
            formData = LinkedMultiValueMap<String, String>().apply {
                add("client_id", request.clientId)
                add("client_secret", request.clientSecret)
                add("grant_type", request.grantType)
            },
        )) {
            is KeycloakResponse.Success -> KeycloakResponse.Success(
                KeycloakAdminTokenData(accessToken = tokenResult.data.accessToken)
            )

            is KeycloakResponse.Error -> KeycloakResponse.Error(
                errorCode = tokenResult.errorCode,
                errorMessage = tokenResult.errorMessage,
            )
        }

    /**
     * Authorization Code를 Access Token으로 교환
     */
    suspend fun exchangeAuthorizationCode(
        code: String,
        redirectUri: String,
        clientId: String,
        clientSecret: String,
    ): KeycloakResponse<KeycloakTokenResponse> =
        executeFormPost(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/token",
            formData = LinkedMultiValueMap<String, String>().apply {
                add("grant_type", "authorization_code")
                add("code", code)
                add("redirect_uri", redirectUri)
                add("client_id", clientId)
                add("client_secret", clientSecret)
            },
        )

    /**
     * Direct Grant - 로그인
     */
    suspend fun login(
        request: KeycloakLoginRequest,
    ): KeycloakResponse<KeycloakTokenResponse> =
        executeFormPost(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/token",
            formData = LinkedMultiValueMap<String, String>().apply {
                add("client_id", request.clientId)
                add("client_secret", request.clientSecret)
                add("grant_type", request.grantType)
                add("username", request.username)
                add("password", request.password)
                add("scope", request.scope)
                request.totp?.let { add("totp", it) }
            },
        )

    /**
     * 토큰 갱신
     */
    suspend fun refreshToken(
        request: KeycloakRefreshTokenRequest,
    ): KeycloakResponse<KeycloakTokenResponse> =
        executeFormPost(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/token",
            formData = LinkedMultiValueMap<String, String>().apply {
                add("client_id", request.clientId)
                add("client_secret", request.clientSecret)
                add("grant_type", request.grantType)
                add("refresh_token", request.refreshToken)
            },
        )

    /**
     * 로그아웃
     */
    suspend fun logout(
        request: KeycloakLogoutRequest,
    ): KeycloakResponse<KeycloakLogoutResponse> =
        executeFormPostWithoutResponse(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/logout",
            formData = LinkedMultiValueMap<String, String>().apply {
                add("client_id", request.clientId)
                add("client_secret", request.clientSecret)
                add("refresh_token", request.refreshToken)
            }
        )

    /**
     * UserInfo 조회
     */
    suspend fun getUserInfo(
        accessToken: String,
    ): KeycloakResponse<KeycloakUserInfoResponse> =
        executeGet(
            uri = "/realms/${keycloakProperties.realm}/protocol/openid-connect/userinfo",
            headers = mapOf(HttpHeaders.AUTHORIZATION to "Bearer $accessToken")
        ) { responseBody ->
            handleSuccessResponse(responseBody, KeycloakUserInfoResponse::class.java)
        }

    // ========================================
    // HTTP 메서드별 실행 메서드
    // ========================================

    /**
     * GET 요청 (응답 본문 반환)
     */
    private suspend fun <T> executeGet(
        uri: String,
        headers: Map<String, String> = emptyMap(),
        responseParser: (String) -> KeycloakResponse<T>
    ): KeycloakResponse<T> = try {
        var requestSpec = keycloakWebClient.get().uri(uri)
        headers.forEach { (key, value) ->
            requestSpec = requestSpec.header(key, value)
        }

        val response = requestSpec.retrieve().awaitBody<String>()
        responseParser(response)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * POST 요청 (Location 헤더 반환)
     */
    private suspend fun <T> executePostWithLocation(
        uri: String,
        headers: Map<String, String> = emptyMap(),
        request: Any,
        responseParser: (String) -> KeycloakResponse<T>
    ): KeycloakResponse<T> = try {
        var requestSpec = keycloakWebClient.post().uri(uri)
        headers.forEach { (key, value) ->
            requestSpec = requestSpec.header(key, value)
        }

        val response = requestSpec
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .toBodilessEntity()
            .awaitSingle()

        val locationHeader = response.headers.getFirst(HttpHeaders.LOCATION)
        responseParser(locationHeader ?: "")
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * POST 요청 (응답 없음)
     */
    private suspend fun executePostWithoutResponse(
        uri: String,
        headers: Map<String, String> = emptyMap(),
        request: Any
    ): KeycloakResponse<KeycloakLogoutResponse> = try {
        var requestSpec = keycloakWebClient.post().uri(uri)
        headers.forEach { (key, value) ->
            requestSpec = requestSpec.header(key, value)
        }

        requestSpec
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(request)
            .retrieve()
            .awaitBodilessEntity()

        KeycloakResponse.Success(KeycloakLogoutResponse)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * PUT 요청 (응답 없음)
     */
    private suspend fun executePutWithoutResponse(
        uri: String,
        headers: Map<String, String> = emptyMap(),
        request: Any? = null
    ): KeycloakResponse<KeycloakLogoutResponse> = try {
        var requestSpec = keycloakWebClient.put().uri(uri)
        headers.forEach { (key, value) ->
            requestSpec = requestSpec.header(key, value)
        }

        if (request != null) {
            requestSpec
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .retrieve()
                .awaitBodilessEntity()
        } else {
            requestSpec.retrieve().awaitBodilessEntity()
        }

        KeycloakResponse.Success(KeycloakLogoutResponse)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * DELETE 요청 (응답 없음)
     */
    private suspend fun executeDeleteWithoutResponse(
        uri: String,
        headers: Map<String, String> = emptyMap()
    ): KeycloakResponse<KeycloakLogoutResponse> = try {
        var requestSpec = keycloakWebClient.delete().uri(uri)
        headers.forEach { (key, value) ->
            requestSpec = requestSpec.header(key, value)
        }

        requestSpec.retrieve().awaitBodilessEntity()
        KeycloakResponse.Success(KeycloakLogoutResponse)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * Form POST 요청 (응답 본문 반환)
     */
    private suspend fun executeFormPost(
        uri: String,
        formData: LinkedMultiValueMap<String, String>
    ): KeycloakResponse<KeycloakTokenResponse> = try {
        val response = keycloakWebClient
            .post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .awaitBody<String>()

        handleSuccessResponse(response, KeycloakTokenResponse::class.java)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    /**
     * Form POST 요청 (응답 없음)
     */
    private suspend fun executeFormPostWithoutResponse(
        uri: String,
        formData: LinkedMultiValueMap<String, String>
    ): KeycloakResponse<KeycloakLogoutResponse> = try {
        keycloakWebClient
            .post()
            .uri(uri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED)
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .awaitBodilessEntity()

        KeycloakResponse.Success(KeycloakLogoutResponse)
    } catch (e: WebClientResponseException) {
        handleHttpError(e)
    } catch (e: Exception) {
        handleGenericError(e)
    }

    // ========================================
    // Helper Methods
    // ========================================

    /**
     * Location 헤더에서 사용자 ID 추출
     */
    private fun extractUserIdFromLocation(
        locationHeader: String?,
    ): String? =
        locationHeader
            ?.let { location ->
                location.substringAfterLast("/")
                    .takeIf { it.isNotBlank() && it.matches(UUID_REGEX) }
            }

    /**
     * 성공 응답 파싱
     */
    private fun <T> handleSuccessResponse(
        response: String,
        dataClass: Class<T>,
    ): KeycloakResponse<T> {
        return try {
            val jsonNode = objectMapper.readTree(response)

            if (jsonNode.has("error")) {
                return KeycloakResponse.Error(
                    errorCode = jsonNode.get("error").asText(),
                    errorMessage = jsonNode.get("error_description")?.asText() ?: "API 요청 실패"
                )
            }

            val data: T = objectMapper.readValue(response, dataClass)
            KeycloakResponse.Success(data)
        } catch (e: Exception) {
            KeycloakResponse.Error("PARSE_ERROR", "응답 파싱 실패: ${e.message}")
        }
    }

    /**
     * HTTP 에러 처리
     */
    private fun handleHttpError(
        e: WebClientResponseException,
    ): KeycloakResponse<Nothing> =
        try {
            val jsonNode = objectMapper.readTree(e.responseBodyAsString)
            if (jsonNode.has("error")) {
                KeycloakResponse.Error(
                    errorCode = jsonNode.get("error").asText(),
                    errorMessage = jsonNode.get("error_description")?.asText() ?: "HTTP 오류"
                )
            } else {
                val errorCode = KeycloakApiErrorCode.fromStatus(e.statusCode.value())
                KeycloakResponse.Error(
                    errorCode = errorCode.code,
                    errorMessage = errorCode.message
                )
            }
        } catch (_: Exception) {
            val errorCode = KeycloakApiErrorCode.fromStatus(e.statusCode.value())
            KeycloakResponse.Error(
                errorCode = errorCode.code,
                errorMessage = "${errorCode.message}: ${e.statusText}"
            )
        }

    /**
     * 일반 예외 처리
     */
    private fun handleGenericError(
        e: Exception,
    ): KeycloakResponse<Nothing> {
        val errorCode = when (e) {
            is java.net.SocketTimeoutException,
            is java.net.ConnectException -> KeycloakApiErrorCode.TIMEOUT

            is java.net.UnknownHostException -> KeycloakApiErrorCode.CONNECTION_ERROR
            else -> KeycloakApiErrorCode.UNKNOWN
        }

        return KeycloakResponse.Error(
            errorCode = errorCode.code,
            errorMessage = "${errorCode.message}: ${e.message ?: "알 수 없는 오류"}"
        )
    }

    companion object {
        private val UUID_REGEX = Regex(
            "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}"
        )
    }
}