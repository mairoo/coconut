package kr.pincoin.api.external.auth.keycloak.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakTokenResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import org.springframework.stereotype.Service

@Service
class KeycloakTokenService(
    private val keycloakApiClient: KeycloakApiClient,
    private val keycloakProperties: KeycloakProperties,
) {
    /**
     * Authorization Code를 Access Token으로 교환
     */
    suspend fun exchangeAuthorizationCode(
        code: String,
        redirectUri: String,
    ): KeycloakResponse<KeycloakTokenResponse> =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(keycloakProperties.timeout) {
                    keycloakApiClient.exchangeAuthorizationCode(
                        code = code,
                        redirectUri = redirectUri,
                        clientId = keycloakProperties.clientId,
                        clientSecret = keycloakProperties.clientSecret,
                    )
                }
            } catch (_: TimeoutCancellationException) {
                handleTimeout("Authorization Code 토큰 교환")
            } catch (e: Exception) {
                handleError(e, "Authorization Code 토큰 교환")
            }
        }

    private fun handleTimeout(
        operation: String,
    ): KeycloakResponse<Nothing> =
        KeycloakResponse.Error(
            errorCode = "TIMEOUT",
            errorMessage = "$operation 요청 시간 초과"
        )

    private fun handleError(
        error: Throwable,
        operation: String,
    ): KeycloakResponse<Nothing> =
        KeycloakResponse.Error(
            errorCode = "SYSTEM_ERROR",
            errorMessage = "${operation} 중 오류 발생: ${error.message ?: "알 수 없는 오류"}"
        )
}