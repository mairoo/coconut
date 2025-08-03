package kr.pincoin.api.app.user.my.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.user.my.request.MyPasswordChangeRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.service.KeycloakPasswordService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Service

@Service
class MyUserProfileService(
    private val keycloakPasswordService: KeycloakPasswordService,
) {
    private val logger = KotlinLogging.logger {}

    fun changeUserPassword(
        user: User,
        request: MyPasswordChangeRequest,
    ): Boolean = runBlocking {
        try {
            // Keycloak 사용자인지 확인
            if (user.keycloakId == null) {
                logger.warn { "레거시 사용자 비밀번호 변경 시도: userId=${user.id}" }
                throw BusinessException(UserErrorCode.LEGACY_USER_PASSWORD_CHANGE_NOT_SUPPORTED)
            }

            // 현재 비밀번호 검증
            validateCurrentPassword(user.email, request.currentPassword)

            // 비밀번호 변경
            changePassword(user.keycloakId.toString(), request.newPassword)

            return@runBlocking true
        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error { "비밀번호 변경 중 예외 발생: userId=${user.id}, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }

    private suspend fun validateCurrentPassword(email: String, currentPassword: String) {
        when (val result = keycloakPasswordService.validateCurrentPassword(email, currentPassword)) {
            is KeycloakResponse.Success -> {
            }

            is KeycloakResponse.Error -> {
                logger.warn { "현재 비밀번호 검증 실패: email=$email, error=${result.errorCode}" }
                throw BusinessException(UserErrorCode.INVALID_CURRENT_PASSWORD)
            }
        }
    }

    private suspend fun changePassword(keycloakUserId: String, newPassword: String) {
        when (val result = keycloakPasswordService.changePasswordByAdmin(
            userId = keycloakUserId,
            newPassword = newPassword,
            temporary = false,
        )) {
            is KeycloakResponse.Success -> {
            }

            is KeycloakResponse.Error -> {
                logger.error { "비밀번호 변경 실패: keycloakUserId=$keycloakUserId, error=${result.errorCode}" }
                throw BusinessException(UserErrorCode.PASSWORD_CHANGE_FAILED)
            }
        }
    }
}