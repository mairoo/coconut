package kr.pincoin.api.app.user.my.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.user.my.request.MyPasswordChangeRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.model.User
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.service.KeycloakPasswordService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Service

@Service
class MyUserProfileService(
    private val userService: UserService,
    private val keycloakPasswordService: KeycloakPasswordService,
) {
    private val logger = KotlinLogging.logger {}

    fun changeUserPassword(
        user: User,
        request: MyPasswordChangeRequest,
    ): Boolean = runBlocking {
        try {
            val userId = user.id!!

            // 1. 사용자 정보 조회
            val user = userService.findUser(userId, UserSearchCriteria())

            // 2. Keycloak 사용자인지 확인
            if (user.keycloakId != null) {
                // Keycloak 사용자: Keycloak에서 비밀번호 변경
                when (val result = keycloakPasswordService.changePasswordByAdmin(
                    userId = user.keycloakId.toString(),
                    newPassword = request.newPassword,
                    temporary = false,
                )) {
                    is KeycloakResponse.Success -> {
                        true
                    }

                    is KeycloakResponse.Error -> {
                        logger.error { "Keycloak 사용자 비밀번호 변경 실패: userId=$userId, error=${result.errorCode}" }
                        throw BusinessException(UserErrorCode.SYSTEM_ERROR)
                    }
                }
            } else {
                // 레거시 사용자는 비밀번호 변경 불가 (Keycloak으로 마이그레이션 필요)
                logger.warn { "레거시 사용자 비밀번호 변경 시도: userId=$userId" }
                throw BusinessException(UserErrorCode.LEGACY_USER_PASSWORD_CHANGE_NOT_SUPPORTED)
            }
        } catch (e: BusinessException) {
            logger.error { "사용자 비밀번호 변경 실패: userId=${user.id}, error=${e.errorCode}" }
            throw e
        } catch (e: Exception) {
            logger.error { "사용자 비밀번호 변경 중 예외 발생: userId=${user.id}, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }
}