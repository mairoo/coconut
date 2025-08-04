package kr.pincoin.api.external.auth.recaptcha.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class RecaptchaErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    VERIFICATION_FAILED(
        HttpStatus.BAD_REQUEST,
        "reCAPTCHA 검증에 실패했습니다",
    ),
    SYSTEM_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "reCAPTCHA 검증 중 시스템 오류가 발생했습니다",
    ),
    TIMEOUT(
        HttpStatus.REQUEST_TIMEOUT,
        "reCAPTCHA 검증 요청 시간이 초과되었습니다",
    ),
}