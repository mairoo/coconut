package kr.pincoin.api.domain.user.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 사용자가 없습니다",
    ),
    ALREADY_EXISTS(
        HttpStatus.BAD_REQUEST,
        "아이디 또는 이메일이 이미 존재합니다",
    ),
    EMAIL_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "이미 가입된 이메일 주소입니다",
    ),
    VERIFICATION_TOKEN_INVALID(
        HttpStatus.BAD_REQUEST,
        "유효하지 않은 인증 토큰입니다",
    ),
    SYSTEM_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "시스템 오류입니다",
    ),
    RECAPTCHA_TOKEN_REQUIRED(
        HttpStatus.BAD_REQUEST,
        "reCAPTCHA 토큰이 필요합니다",
    ),
    RECAPTCHA_VERIFICATION_FAILED(
        HttpStatus.BAD_REQUEST,
        "reCAPTCHA 검증에 실패했습니다",
    ),
    EMAIL_DOMAIN_NOT_ALLOWED(
        HttpStatus.BAD_REQUEST,
        "허용되지 않은 이메일 도메인입니다",
    ),
    DAILY_SIGNUP_LIMIT_EXCEEDED(
        HttpStatus.TOO_MANY_REQUESTS,
        "일일 가입 제한을 초과했습니다",
    ),
    SIGNUP_IN_PROGRESS(
        HttpStatus.CONFLICT,
        "이미 가입 진행 중인 이메일입니다",
    ),
    EMAIL_SEND_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "이메일 발송에 실패했습니다",
    ),
}