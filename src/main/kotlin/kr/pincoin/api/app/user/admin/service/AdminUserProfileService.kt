package kr.pincoin.api.app.user.admin.service

import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.runBlocking
import kr.pincoin.api.app.user.admin.request.AdminPasswordChangeRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.service.KeycloakPasswordService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import kr.pincoin.api.infra.user.repository.projection.UserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminUserProfileService(
    private val userService: UserService,
    private val keycloakPasswordService: KeycloakPasswordService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 관리자용 사용자 목록 조회 (페이징)
     */
    fun getUsersWithProfile(
        criteria: UserSearchCriteria,
        pageable: Pageable,
    ): Page<UserProfileProjection> =
        userService.findUsersWithProfile(criteria, pageable)

    /**
     * 관리자용 사용자 상세 조회
     */
    fun getUserWithProfile(
        userId: Int,
        criteria: UserSearchCriteria = UserSearchCriteria(),
    ): UserProfileProjection =
        userService.findUserWithProfile(userId, criteria)

    /**
     * 관리자가 사용자 비밀번호 강제 변경
     * Keycloak 사용자와 레거시 사용자 모두 지원
     */
    fun changeUserPassword(
        userId: Int,
        request: AdminPasswordChangeRequest,
    ): Boolean = runBlocking {
        try {
            // 1. 사용자 정보 조회
            val user = userService.findUser(userId, UserSearchCriteria())

            // 2. Keycloak 사용자인지 확인
            if (user.keycloakId != null) {
                // Keycloak 사용자: Keycloak에서 비밀번호 변경
                when (val result = keycloakPasswordService.changePasswordByAdmin(
                    userId = user.keycloakId.toString(),
                    newPassword = request.newPassword,
                    temporary = request.temporary
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
            logger.error { "사용자 비밀번호 변경 실패: userId=$userId, error=${e.errorCode}" }
            throw e
        } catch (e: Exception) {
            logger.error { "사용자 비밀번호 변경 중 예외 발생: userId=$userId, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 관리자가 사용자에게 비밀번호 재설정 강제 (Required Action)
     * 현재 비밀번호는 유지하고, 다음 로그인 시 비밀번호 변경 화면만 표시
     * Keycloak의 UPDATE_PASSWORD 액션을 사용
     */
    fun forcePasswordReset(userId: Int): Boolean = runBlocking {
        try {
            // 1. 사용자 정보 조회
            val user = userService.findUser(userId, UserSearchCriteria())

            // 2. Keycloak 사용자인지 확인
            if (user.keycloakId != null) {
                // Keycloak 사용자: 비밀번호 재설정 액션 추가
                when (val result = keycloakPasswordService.addPasswordResetAction(user.keycloakId.toString())) {
                    is KeycloakResponse.Success -> {
                        true
                    }

                    is KeycloakResponse.Error -> {
                        logger.error { "비밀번호 재설정 액션 추가 실패: userId=$userId, error=${result.errorCode}" }
                        throw BusinessException(UserErrorCode.SYSTEM_ERROR)
                    }
                }
            } else {
                // 레거시 사용자는 지원하지 않음
                logger.warn { "레거시 사용자 비밀번호 재설정 강제 시도: userId=$userId" }
                throw BusinessException(UserErrorCode.LEGACY_USER_PASSWORD_CHANGE_NOT_SUPPORTED)
            }
        } catch (e: BusinessException) {
            logger.error { "비밀번호 재설정 강제 실패: userId=$userId, error=${e.errorCode}" }
            throw e
        } catch (e: Exception) {
            logger.error { "비밀번호 재설정 강제 중 예외 발생: userId=$userId, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }
}