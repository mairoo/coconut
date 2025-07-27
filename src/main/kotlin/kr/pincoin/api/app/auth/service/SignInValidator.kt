package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
import kr.pincoin.api.domain.coordinator.user.TotpResourceCoordinator
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Component

/**
 * 로그인 검증 전담 서비스
 *
 * 로그인 요청에 대한 보안 검증을 담당합니다.
 * 무작위 공격 방어와 기본적인 입력값 검증을 수행합니다.
 *
 * **주요 검증 항목:**
 * 1. reCAPTCHA 검증 (봇/스팸 차단)
 * 2. 2FA OTP 검증 (사용자별 2FA 활성화 상태 확인)
 * 3. 향후 확장 가능한 추가 보안 검증
 *    - IP별 로그인 시도 제한
 *    - 계정별 로그인 실패 카운트
 *    - 디바이스 핑거프린팅
 */
@Component
class SignInValidator(
    private val recaptchaService: RecaptchaService,
    private val totpResourceCoordinator: TotpResourceCoordinator,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 로그인 요청 전체 검증 (TOTP 지원)
     *
     * 로그인 요청에 대한 모든 보안 검증을 수행합니다.
     * 점진적 마이그레이션을 고려하여 필수 검증만 우선 구현합니다.
     *
     * **현재 구현된 검증:**
     * 1. reCAPTCHA 검증 (무작위 공격 방어)
     * 2. 2FA OTP 검증 (사용자별 활성화 상태 확인)
     *
     * **향후 추가 예정:**
     * 3. IP별 로그인 시도 제한 (브루트포스 방어)
     * 4. 계정별 연속 실패 제한 (계정 보호)
     *
     * @param request 로그인 요청 정보
     * @param httpServletRequest HTTP 요청 정보
     * @throws BusinessException 검증 실패 시
     */
    suspend fun validateSignInRequest(
        request: SignInRequest,
        httpServletRequest: HttpServletRequest,
    ) {
        // 1. reCAPTCHA 검증 (봇/스팸 차단)
        validateRecaptcha(request.recaptchaToken)

        // 2. 2FA OTP 검증 (사용자별 2FA 활성화 상태 확인)
        validate2FA(request.email, request.totpCode)

        // TODO: 향후 추가할 검증들
        // 3. IP별 로그인 시도 제한
        // validateIpLoginLimit(clientInfo.ipAddress)

        // 4. 계정별 연속 실패 제한
        // validateAccountLockout(request.email)
    }

    /**
     * reCAPTCHA 검증
     *
     * Google reCAPTCHA v3를 사용하여 봇과 스팸 로그인 시도를 차단합니다.
     * 회원가입보다 약간 낮은 임계값을 사용합니다.
     *
     * @param recaptchaToken 클라이언트에서 전송된 reCAPTCHA 토큰
     * @param minScore 최소 허용 점수 (0.0~1.0, 기본값 0.5)
     * @throws BusinessException 토큰 누락 또는 검증 실패 시
     */
    private suspend fun validateRecaptcha(
        recaptchaToken: String?,
        minScore: Double = 0.5, // 로그인은 회원가입보다 낮은 임계값 사용
    ) {
        // 토큰 존재 여부 확인
        if (recaptchaToken.isNullOrBlank()) {
            logger.warn { "로그인 시 reCAPTCHA 토큰이 제공되지 않음" }
            throw BusinessException(UserErrorCode.RECAPTCHA_TOKEN_REQUIRED)
        }

        // reCAPTCHA 검증 수행
        val result = recaptchaService.verifyV3(recaptchaToken, minScore)

        if (result is RecaptchaResponse.Error) {
            logger.warn { "로그인 reCAPTCHA 검증 실패 - 코드: ${result.errorCode}, 메시지: ${result.errorMessage}" }
            throw BusinessException(UserErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }
    }

    /**
     * 2FA OTP 검증
     *
     * 사용자의 2FA 활성화 여부를 확인하고,
     * 활성화된 경우 TOTP 코드 입력을 필수로 요구합니다.
     *
     * **검증 로직:**
     * 1. 사용자의 2FA 활성화 상태 확인
     * 2. 활성화된 경우 TOTP 코드 필수 검증
     * 3. 비활성화된 경우 TOTP 코드 무시
     *
     * **에러 처리:**
     * - 2FA 상태 확인 실패 시 로그인 허용 (가용성 우선)
     * - 사용자 없음은 로그인 단계에서 처리
     */
    private suspend fun validate2FA(email: String, totpCode: String?) {
        try {
            // 1. 사용자의 2FA 활성화 상태 확인
            val totpStatus = totpResourceCoordinator.getTotpStatus(email)

            if (totpStatus.enabled) {
                // 2FA가 활성화된 사용자인 경우 TOTP 코드 필수
                if (totpCode.isNullOrBlank()) {
                    logger.warn { "2FA 활성화된 사용자의 TOTP 코드 누락: email=$email" }
                    throw BusinessException(UserErrorCode.TOTP_CODE_REQUIRED)
                }

                logger.debug { "2FA 활성화된 사용자 로그인 시도: email=$email" }
            } else {
                // 2FA 비활성화된 사용자는 TOTP 코드 불필요
                if (!totpCode.isNullOrBlank()) {
                    logger.debug { "2FA 비활성화된 사용자가 TOTP 코드 전송: email=$email (무시됨)" }
                }
            }

        } catch (e: BusinessException) {
            when (e.errorCode) {
                UserErrorCode.TOTP_CODE_REQUIRED -> throw e
                UserErrorCode.NOT_FOUND -> {
                    // 사용자가 존재하지 않는 경우 - 로그인 단계에서 처리
                    logger.debug { "2FA 검증 중 사용자 없음: email=$email" }
                }

                else -> {
                    // 2FA 상태 확인 실패 시 로그인 허용 (가용성 우선)
                    logger.warn { "2FA 상태 확인 실패하지만 로그인 허용: email=$email, error=${e.errorCode}" }
                }
            }
        } catch (e: Exception) {
            // 예기치 못한 오류 시 로그인 허용 (가용성 우선)
            logger.warn { "2FA 검증 중 예기치 못한 오류, 로그인 허용: email=$email, error=${e.message}" }
        }
    }

    // TODO: 향후 구현될 추가 검증 메서드들

    /**
     * IP별 로그인 시도 제한 검증
     *
     * 동일 IP에서의 과도한 로그인 시도를 차단합니다.
     * Redis를 활용한 슬라이딩 윈도우 방식 구현 예정.
     */
    /*
    private fun validateIpLoginLimit(ip: String) {
        // Redis 기반 슬라이딩 윈도우로 IP별 로그인 시도 횟수 체크
        // 예: 10분간 20회 시도 제한
    }
    */

    /**
     * 계정별 연속 실패 제한 검증
     *
     * 특정 계정에 대한 연속 로그인 실패를 추적하고 제한합니다.
     * 지수 백오프와 임시 잠금 정책 적용 예정.
     */
    /*
    private fun validateAccountLockout(email: String) {
        // 계정별 로그인 실패 횟수 체크
        // 5회 실패 시 15분 잠금, 10회 실패 시 1시간 잠금 등
    }
    */
}