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
    INVALID_CREDENTIALS(
        HttpStatus.BAD_REQUEST,
        "잘못된 이메일 또는 비밀번호입니다",
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

    // 마이그레이션 관련 에러 코드
    ALREADY_MIGRATED(
        HttpStatus.CONFLICT,
        "이미 마이그레이션이 완료된 사용자입니다",
    ),

    // TOTP 2FA 관련 에러 코드들
    TOTP_CODE_REQUIRED(
        HttpStatus.BAD_REQUEST,
        "2FA가 활성화된 계정입니다. TOTP 코드를 입력해주세요",
    ),
    INVALID_TOTP_CODE(
        HttpStatus.BAD_REQUEST,
        "TOTP 코드가 올바르지 않습니다",
    ),
    TOTP_ALREADY_ENABLED(
        HttpStatus.CONFLICT,
        "이미 2FA가 활성화되어 있습니다",
    ),
    TOTP_SETUP_SESSION_EXPIRED(
        HttpStatus.BAD_REQUEST,
        "2FA 설정 세션이 만료되었습니다",
    ),
    KEYCLOAK_NOT_LINKED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 계정이 연결되지 않았습니다",
    ),
    TOTP_SETUP_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "2FA 설정에 실패했습니다",
    ),
    TOTP_DISABLE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "2FA 비활성화에 실패했습니다",
    ),

    // 비밀번호 변경 관련 에러 코드 추가
    LEGACY_USER_PASSWORD_CHANGE_NOT_SUPPORTED(
        HttpStatus.BAD_REQUEST,
        "레거시 사용자는 비밀번호 변경이 지원되지 않습니다. Keycloak 마이그레이션 후 이용해주세요",
    ),

    // OAuth2 Authorization Code Flow 관련 에러 코드
    INVALID_REDIRECT_URI(
        HttpStatus.BAD_REQUEST,
        "허용되지 않은 redirect_uri입니다",
    ),
    TOKEN_EXCHANGE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Authorization Code를 토큰으로 교환하는데 실패했습니다",
    ),
    INVALID_STATE_PARAMETER(
        HttpStatus.BAD_REQUEST,
        "잘못된 state 파라미터입니다. CSRF 공격이 감지되었습니다",
    ),
    INVALID_AUTHORIZATION_CODE(
        HttpStatus.BAD_REQUEST,
        "유효하지 않은 인증 코드입니다",
    ),
    INVALID_CLIENT_CREDENTIALS(
        HttpStatus.UNAUTHORIZED,
        "유효하지 않은 클라이언트 인증 정보입니다",
    ),
}