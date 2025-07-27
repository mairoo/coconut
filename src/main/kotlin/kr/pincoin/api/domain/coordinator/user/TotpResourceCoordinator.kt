package kr.pincoin.api.domain.coordinator.user

import io.github.oshai.kotlinlogging.KotlinLogging
import kr.pincoin.api.app.user.common.request.TotpSetupRequest
import kr.pincoin.api.app.user.common.response.TotpSetupResponse
import kr.pincoin.api.app.user.common.response.TotpStatusResponse
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.domain.user.service.UserService
import kr.pincoin.api.external.auth.keycloak.api.response.KeycloakResponse
import kr.pincoin.api.external.auth.keycloak.service.KeycloakTotpService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.user.repository.criteria.UserSearchCriteria
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.util.concurrent.TimeUnit

@Service
class TotpResourceCoordinator(
    private val keycloakTotpService: KeycloakTotpService,
    private val userService: UserService,
    private val redisTemplate: RedisTemplate<String, String>,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 관리자가 특정 사용자에게 2FA 강제 설정
     */
    suspend fun forceUserTotpSetup(
        userEmail: String,
    ) =
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: throw BusinessException(UserErrorCode.KEYCLOAK_NOT_LINKED)

            // 3. Keycloak에서 TOTP 필수 액션 추가
            when (val result = keycloakTotpService.addTotpRequiredAction(keycloakId)) {
                is KeycloakResponse.Success -> {
                    logger.info { "사용자 2FA 강제 설정 완료: email=$userEmail, keycloakId=$keycloakId" }
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
     * 사용자 자발적 2FA 설정 시작 (QR 코드 생성)
     */
    suspend fun startTotpSetup(
        userEmail: String,
    ): TotpSetupResponse {
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: throw BusinessException(UserErrorCode.KEYCLOAK_NOT_LINKED)

            // 3. 이미 TOTP가 활성화되어 있는지 확인
            when (val statusResult = keycloakTotpService.isUserTotpEnabled(keycloakId)) {
                is KeycloakResponse.Success -> {
                    if (statusResult.data) {
                        throw BusinessException(UserErrorCode.TOTP_ALREADY_ENABLED)
                    }
                }

                is KeycloakResponse.Error -> {
                    logger.warn { "TOTP 상태 확인 실패: email=$userEmail, error=${statusResult.errorCode}" }
                    // 상태 확인 실패해도 설정은 진행
                }
            }

            // 4. TOTP Secret 생성 및 QR 코드 준비
            val setupData = keycloakTotpService.generateTotpSetupData(userEmail)
            val backupCodes = generateBackupCodes()

            // 5. Redis에 임시 저장 (10분 TTL)
            saveTempTotpData(userEmail, setupData.secret, backupCodes)

            logger.info { "사용자 2FA 설정 시작: email=$userEmail, keycloakId=$keycloakId" }

            return TotpSetupResponse(
                qrCodeUrl = setupData.qrCodeUrl,
                manualEntryKey = setupData.manualEntryKey,
                backupCodes = backupCodes,
            )

        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error { "2FA 설정 시작 중 예기치 못한 오류: email=$userEmail, error=${e.message}" }
            throw BusinessException(UserErrorCode.SYSTEM_ERROR)
        }
    }

    /**
     * 2FA 설정 완료 (OTP 코드 검증)
     */
    suspend fun completeTotpSetup(
        userEmail: String,
        request: TotpSetupRequest,
    ) =
        try {
            // 1. 사용자 조회
            val user = userService.findUser(UserSearchCriteria(email = userEmail, isActive = true))

            // 2. Keycloak ID 확인
            val keycloakId = user.keycloakId?.toString()
                ?: throw BusinessException(UserErrorCode.KEYCLOAK_NOT_LINKED)

            // 3. Redis에서 임시 저장된 Secret 조회
            val tempData = getTempTotpData(userEmail)
                ?: throw BusinessException(UserErrorCode.TOTP_SETUP_SESSION_EXPIRED)

            // 4. OTP 코드 검증 (형식 확인 후 Keycloak에서 실제 검증)
            if (!validateTotpCodeFormat(request.otpCode)) {
                throw BusinessException(UserErrorCode.INVALID_TOTP_CODE)
            }

            // 5. Keycloak에 TOTP 인증정보 저장
            when (val result = keycloakTotpService.saveTotpCredential(keycloakId, tempData.secret)) {
                is KeycloakResponse.Success -> {
                    // 6. 임시 저장된 데이터 삭제
                    deleteTempTotpData(userEmail)

                    logger.info { "사용자 2FA 설정 완료: email=$userEmail, keycloakId=$keycloakId" }
                }

                is KeycloakResponse.Error -> {
                    logger.error { "TOTP 설정 완료 실패: email=$userEmail, error=${result.errorCode}" }
                    throw BusinessException(UserErrorCode.TOTP_SETUP_FAILED)
                }
            }

        } catch (e: BusinessException) {
            throw e
        } catch (e: Exception) {
            logger.error { "2FA 설정 완료 중 예기치 못한 오류: email=$userEmail, error=${e.message}" }
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
                    logger.info { "사용자 2FA 비활성화 완료: email=$userEmail, keycloakId=$keycloakId" }
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

    /**
     * 백업 코드 생성 (관리자 지원으로 대체)
     *
     * 디바이스 분실 등 문제 발생 시 관리자가 직접 2FA 해제하여 지원하므로
     * 백업 코드는 제공하지 않습니다.
     */
    private fun generateBackupCodes(
    ): List<String> =
        listOf(
            "관리자에게 문의하세요",
            "디바이스 분실 시 관리자가 2FA를 해제해드립니다"
        )

    /**
     * TOTP 코드 형식 검증
     * 실제 TOTP 검증은 Keycloak에서 처리됩니다.
     */
    private fun validateTotpCodeFormat(
        otpCode: String,
    ): Boolean =
        otpCode.matches(Regex("^[0-9]{6}$"))

    /**
     * 임시 TOTP 데이터 저장
     */
    private fun saveTempTotpData(
        userEmail: String,
        secret: String,
        backupCodes: List<String>,
    ) {
        val key = "totp_setup:$userEmail"
        val data = "$secret|${backupCodes.joinToString(",")}"
        redisTemplate.opsForValue().set(key, data, 10, TimeUnit.MINUTES)
    }

    /**
     * 임시 TOTP 데이터 조회
     */
    private fun getTempTotpData(
        userEmail: String,
    ): TempTotpData? {
        val key = "totp_setup:$userEmail"
        val data = redisTemplate.opsForValue().get(key) ?: return null

        val parts = data.split("|")
        if (parts.size != 2) return null

        val secret = parts[0]
        val backupCodes = parts[1].split(",")

        return TempTotpData(secret, backupCodes)
    }

    /**
     * 임시 TOTP 데이터 삭제
     */
    private fun deleteTempTotpData(
        userEmail: String,
    ) {
        val key = "totp_setup:$userEmail"
        redisTemplate.delete(key)
    }

    /**
     * 임시 TOTP 데이터 클래스
     */
    private data class TempTotpData(
        val secret: String,
        val backupCodes: List<String>,
    )
}