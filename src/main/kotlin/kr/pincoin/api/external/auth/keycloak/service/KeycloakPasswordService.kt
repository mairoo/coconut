package kr.pincoin.api.external.auth.keycloak.service

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakCreateUserRequest
import kr.pincoin.api.external.auth.keycloak.api.request.KeycloakUpdateUserRequest
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakLogoutResponse
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.properties.KeycloakProperties
import org.springframework.stereotype.Service

@Service
class KeycloakPasswordService(
    private val keycloakApiClient: KeycloakApiClient,
    private val keycloakAdminService: KeycloakAdminService,
    private val keycloakProperties: KeycloakProperties,
) {
    /**
     * 관리자가 사용자 비밀번호 변경 (Admin API)
     * 현재 비밀번호 확인 없이 강제 변경 가능
     */
    suspend fun changePasswordByAdmin(
        userId: String,
        newPassword: String,
        temporary: Boolean = false
    ): KeycloakResponse<KeycloakLogoutResponse> =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(keycloakProperties.timeout) {
                    // 1. Admin 토큰 획득
                    val adminToken = when (val adminResult = keycloakAdminService.getAdminToken()) {
                        is KeycloakResponse.Success -> adminResult.data.accessToken
                        is KeycloakResponse.Error -> return@withTimeout KeycloakResponse.Error(
                            adminResult.errorCode,
                            adminResult.errorMessage
                        )
                    }

                    // 2. 비밀번호 변경 요청
                    val request = KeycloakUpdateUserRequest(
                        credentials = listOf(
                            KeycloakCreateUserRequest.KeycloakCredential(
                                type = "password",
                                value = newPassword,
                                temporary = temporary
                            )
                        )
                    )

                    keycloakApiClient.updateUser(adminToken, userId, request)
                }
            } catch (_: TimeoutCancellationException) {
                handleTimeout("관리자 비밀번호 변경")
            } catch (e: Exception) {
                handleError(e, "관리자 비밀번호 변경")
            }
        }

    /**
     * 비밀번호 재설정 필수 액션 추가
     * 사용자가 다음 로그인 시 비밀번호 변경 화면 표시
     */
    suspend fun addPasswordResetAction(userId: String): KeycloakResponse<KeycloakLogoutResponse> =
        withContext(Dispatchers.IO) {
            try {
                withTimeout(keycloakProperties.timeout) {
                    val adminToken = when (val adminResult = keycloakAdminService.getAdminToken()) {
                        is KeycloakResponse.Success -> adminResult.data.accessToken
                        is KeycloakResponse.Error -> return@withTimeout KeycloakResponse.Error(
                            adminResult.errorCode,
                            adminResult.errorMessage
                        )
                    }

                    keycloakApiClient.setUserRequiredActions(adminToken, userId, listOf("UPDATE_PASSWORD"))
                }
            } catch (_: TimeoutCancellationException) {
                handleTimeout("비밀번호 재설정 액션 추가")
            } catch (e: Exception) {
                handleError(e, "비밀번호 재설정 액션 추가")
            }
        }

    private fun handleTimeout(
        operation: String,
    ): KeycloakResponse<Nothing> =
        KeycloakResponse.Error(
            errorCode = "TIMEOUT",
            errorMessage = "$operation 요청 시간 초과",
        )

    private fun handleError(
        error: Throwable,
        operation: String,
    ): KeycloakResponse<Nothing> =
        KeycloakResponse.Error(
            errorCode = "SYSTEM_ERROR",
            errorMessage = "$operation 중 오류 발생: ${error.message ?: "알 수 없는 오류"}",
        )
}