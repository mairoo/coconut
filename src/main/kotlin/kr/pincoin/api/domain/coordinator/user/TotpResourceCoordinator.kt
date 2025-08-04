package kr.pincoin.api.domain.coordinator.user

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.app.user.common.response.TotpStatusResponse
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTotpService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.stereotype.Service

@Service
class TotpResourceCoordinator(
    private val keycloakTotpService: KeycloakTotpService,
    private val userService: UserService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 관리자가 특정 사용자에게 2FA 강제 설정
     */
    suspend fun forceUserTotpSetup(
        userEmail: String,
    ): String =
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: throw BusinessException(UserErrorCode.KEYCLOAK_NOT_LINKED)

            // 3. Keycloak에서 TOTP 필수 액션 추가
            when (val result = keycloakTotpService.addTotpRequiredAction(keycloakId)) {
                is KeycloakResponse.Success -> {
                    "사용자 '$userEmail'에게 2FA 설정이 강제되었습니다. 해당 사용자는 다음 로그인 시 2FA 설정이 필요합니다."
                }

                is KeycloakResponse.Error -> {
                    logger.error { "사용자 2FA 강제 설정 실패: email=$userEmail, error=${result.errorCode}" }
                    throw BusinessException(UserErrorCode.TOTP_SETUP_FAILED)
                }
            }

        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error { "2FA 강제 설정 중 예기치 못한 오류: email=$userEmail, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }

    /**
     * 사용자의 2FA 활성화 상태 조회
     */
    suspend fun getTotpStatus(
        userEmail: String,
    ): TotpStatusResponse {
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: return TotpStatusResponse.Companion.disabled()

            // 3. TOTP 활성화 상태 확인
            return when (val result = keycloakTotpService.isUserTotpEnabled(keycloakId)) {
                is KeycloakResponse.Success -> {
                    if (result.data) {
                        TotpStatusResponse.Companion.enabled()
                    } else {
                        TotpStatusResponse.Companion.disabled()
                    }
                }

                is KeycloakResponse.Error -> {
                    logger.warn { "TOTP 상태 확인 실패: email=$userEmail, error=${result.errorCode}" }
                    TotpStatusResponse.Companion.disabled() // 확인 실패 시 비활성화로 간주
                }
            }

        } catch (e: Exception) {
            logger.warn { "2FA 상태 조회 중 오류: email=$userEmail, error=${e.message}" }
            return TotpStatusResponse.Companion.disabled()
        }
    }

    /**
     * 사용자의 2FA 비활성화
     */
    suspend fun disableTotp(
        userEmail: String,
    ) =
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: throw BusinessException(UserErrorCode.KEYCLOAK_NOT_LINKED)

            // 3. Keycloak에서 TOTP 인증정보 삭제
            when (val result = keycloakTotpService.deleteTotpCredential(keycloakId)) {
                is KeycloakResponse.Success -> {
                    "관리자에 의해 사용자 '$userEmail'의 2FA가 비활성화되었습니다. 보안을 위해 즉시 비밀번호 변경을 권장합니다."
                }

                is KeycloakResponse.Error -> {
                    logger.error { "TOTP 비활성화 실패: email=$userEmail, error=${result.errorCode}" }
                    throw BusinessException(UserErrorCode.TOTP_DISABLE_FAILED)
                }
            }

        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error { "2FA 비활성화 중 예기치 못한 오류: email=$userEmail, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
}