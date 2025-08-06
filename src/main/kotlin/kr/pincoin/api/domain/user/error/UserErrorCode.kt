package kr.pincoin.api.domain.user.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {

    // ============================================================
    // 기본 사용자 관련 에러 코드
    // ============================================================
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

    // ============================================================
    // 회원가입 관련 에러 코드
    // ============================================================
    VERIFICATION_TOKEN_INVALID(
        HttpStatus.BAD_REQUEST,
        "유효하지 않은 인증 토큰입니다",
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

    // ============================================================
    // reCAPTCHA 관련 에러 코드
    // ============================================================
    RECAPTCHA_TOKEN_REQUIRED(
        HttpStatus.BAD_REQUEST,
        "reCAPTCHA 토큰이 필요합니다",
    ),

    // ============================================================
    // 비밀번호 관련 에러 코드
    // ============================================================
    LEGACY_USER_PASSWORD_CHANGE_NOT_SUPPORTED(
        HttpStatus.BAD_REQUEST,
        "레거시 사용자는 비밀번호 변경이 지원되지 않습니다. Keycloak 마이그레이션 후 이용해주세요",
    ),
    INVALID_CURRENT_PASSWORD(
        HttpStatus.BAD_REQUEST,
        "현재 비밀번호가 올바르지 않습니다",
    ),
    PASSWORD_CHANGE_FAILED(
        HttpStatus.BAD_REQUEST,
        "비밀번호 변경하는데 실패했습니다",
    ),

    // ============================================================
    // 2FA/TOTP 관련 에러 코드
    // ============================================================
    TOTP_SETUP_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "2FA 설정에 실패했습니다",
    ),
    TOTP_DISABLE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "2FA 비활성화에 실패했습니다",
    ),

    // ============================================================
    // 마이그레이션 관련 에러 코드
    // ============================================================
    MIGRATION_REQUIRED(
        HttpStatus.BAD_REQUEST,
        "기존 계정이 존재합니다. 먼저 계정 마이그레이션을 진행해주세요",
    ),
    ALREADY_MIGRATED(
        HttpStatus.CONFLICT,
        "이미 마이그레이션이 완료된 사용자입니다",
    ),
    DATA_INTEGRITY_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "사용자 데이터 정합성 오류가 감지되었습니다. 관리자에게 문의하세요",
    ),

    // ============================================================
    // Keycloak 연동 관련 에러 코드
    // ============================================================
    KEYCLOAK_NOT_LINKED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 계정이 연결되지 않았습니다",
    ),

    // ============================================================
    // OAuth2/소셜 로그인 관련 에러 코드
    // ============================================================
    INVALID_REDIRECT_URI(
        HttpStatus.BAD_REQUEST,
        "허용되지 않은 redirect_uri입니다",
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
    TOKEN_EXCHANGE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Authorization Code를 토큰으로 교환하는데 실패했습니다",
    ),

    // ============================================================
    // 소셜 계정 검증 관련 에러 코드
    // ============================================================
    EMAIL_NOT_PROVIDED(
        HttpStatus.BAD_REQUEST,
        "소셜 계정에서 이메일 정보를 제공하지 않습니다",
    ),
    EMAIL_NOT_VERIFIED(
        HttpStatus.BAD_REQUEST,
        "이메일이 검증되지 않은 소셜 계정입니다",
    ),
    USER_INFO_RETRIEVAL_FAILED(
        HttpStatus.BAD_REQUEST,
        "사용자 정보 조회에 실패했습니다",
    ),

    // ============================================================
    // 시스템 에러
    // ============================================================
    SYSTEM_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "시스템 오류입니다",
    ),
}