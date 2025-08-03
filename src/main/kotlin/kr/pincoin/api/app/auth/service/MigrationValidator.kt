package kr.pincoin.api.app.auth.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.auth.request.MigrationRequest
import kr.pincoin.api.domain.user.error.UserErrorCode
import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaResponse
import kr.pincoin.api.external.auth.recaptcha.service.RecaptchaService
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.stereotype.Component

/**
 * 마이그레이션 검증 전담 서비스
 *
 * 레거시 사용자 마이그레이션 요청에 대한 보안 검증을 담당합니다.
 * 무작위 공격 방어와 기본적인 입력값 검증을 수행합니다.
 *
 * **주요 검증 항목:**
 * 1. reCAPTCHA 검증 (봇/스팸 차단)
 * 2. 향후 확장 가능한 추가 보안 검증
 *    - IP별 마이그레이션 시도 제한
 *    - 계정별 마이그레이션 실패 카운트
 */
@Component
class MigrationValidator(
    private val recaptchaService: RecaptchaService,
) {
    private val logger = KotlinLogging.logger {}

    /**
     * 마이그레이션 요청 전체 검증
     *
     * 마이그레이션 요청에 대한 모든 보안 검증을 수행합니다.
     * 점진적 마이그레이션을 고려하여 필수 검증만 우선 구현합니다.
     *
     * **현재 구현된 검증:**
     * 1. reCAPTCHA 검증 (무작위 공격 방어)
     *
     * **향후 추가 예정:**
     * 2. IP별 마이그레이션 시도 제한 (브루트포스 방어)
     * 3. 계정별 연속 실패 제한 (계정 보호)
     */
    suspend fun validateMigrationRequest(
        request: MigrationRequest,
        httpServletRequest: HttpServletRequest,
    ) {
        // 1. reCAPTCHA 검증 (봇/스팸 차단)
        validateRecaptcha(request.recaptchaToken)

        // TODO: 향후 추가할 검증들
        // 2. IP별 마이그레이션 시도 제한
        // validateIpMigrationLimit(clientInfo.ipAddress)

        // 3. 계정별 연속 실패 제한
        // validateAccountLockout(request.email)
    }

    /**
     * reCAPTCHA 검증
     *
     * Google reCAPTCHA v3를 사용하여 봇과 스팸 마이그레이션 시도를 차단합니다.
     * 로그인보다 약간 낮은 임계값을 사용합니다.
     */
    private suspend fun validateRecaptcha(
        recaptchaToken: String?,
        minScore: Double = 0.5, // 마이그레이션은 로그인과 동일한 임계값 사용
    ) {
        // 토큰 존재 여부 확인
        if (recaptchaToken.isNullOrBlank()) {
            logger.warn { "마이그레이션 시 reCAPTCHA 토큰이 제공되지 않음" }
            throw BusinessException(UserErrorCode.RECAPTCHA_TOKEN_REQUIRED)
        }

        // reCAPTCHA 검증 수행
        val result = recaptchaService.verifyV3(recaptchaToken, minScore)

        if (result is RecaptchaResponse.Error) {
            logger.warn { "마이그레이션 reCAPTCHA 검증 실패 - 코드: ${result.errorCode}, 메시지: ${result.errorMessage}" }
            throw BusinessException(UserErrorCode.RECAPTCHA_VERIFICATION_FAILED)
        }
    }

    // TODO: 향후 구현될 추가 검증 메서드들

    /**
     * IP별 마이그레이션 시도 제한 검증
     *
     * 동일 IP에서의 과도한 마이그레이션 시도를 차단합니다.
     * Redis를 활용한 슬라이딩 윈도우 방식 구현 예정.
     */
    /*
    private fun validateIpMigrationLimit(ip: String) {
        // Redis 기반 슬라이딩 윈도우로 IP별 마이그레이션 시도 횟수 체크
        // 예: 10분간 10회 시도 제한
    }
    */

    /**
     * 계정별 연속 실패 제한 검증
     *
     * 특정 계정에 대한 연속 마이그레이션 실패를 추적하고 제한합니다.
     * 지수 백오프와 임시 잠금 정책 적용 예정.
     */
    /*
    private fun validateAccountLockout(email: String) {
        // 계정별 마이그레이션 실패 횟수 체크
        // 5회 실패 시 15분 잠금 등
    }
    */
}