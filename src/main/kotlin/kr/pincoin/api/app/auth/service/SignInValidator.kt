package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.SignInRequest
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
 * 2. 향후 확장 가능한 추가 보안 검증
 *    - IP별 로그인 시도 제한
 *    - 계정별 로그인 실패 카운트
 *    - 디바이스 핑거프린팅
 *    - 2FA OTP 검증
 */
@Component
class SignInValidator(
    private val recaptchaService: RecaptchaService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 로그인 요청 전체 검증
     *
     * 로그인 요청에 대한 모든 보안 검증을 수행합니다.
     * 점진적 마이그레이션을 고려하여 필수 검증만 우선 구현합니다.
     *
     * **현재 구현된 검증:**
     * 1. reCAPTCHA 검증 (무작위 공격 방어)
     *
     * **향후 추가 예정:**
     * 2. IP별 로그인 시도 제한 (브루트포스 방어)
     * 3. 계정별 연속 실패 제한 (계정 보호)
     * 4. 2FA OTP 검증 (추가 보안)
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

        // TODO: 향후 추가할 검증들
        // 2. IP별 로그인 시도 제한
        // validateIpLoginLimit(clientInfo.ipAddress)

        // 3. 계정별 연속 실패 제한
        // validateAccountLockout(request.email)

        // 4. 2FA OTP 검증 (OTP 필요한 계정의 경우)
        // validate2FA(request.email, request.otpCode)
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

        logger.debug { "로그인 reCAPTCHA 검증 성공" }
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

    /**
     * 2FA OTP 검증
     *
     * Google Authenticator 등의 TOTP 코드를 검증합니다.
     * 사용자 설정에 따라 선택적으로 적용됩니다.
     */
    /*
    private fun validate2FA(email: String, otpCode: String?) {
        // 해당 사용자의 2FA 설정 확인
        // OTP 코드 검증 (Google Authenticator TOTP)
    }
    */
}