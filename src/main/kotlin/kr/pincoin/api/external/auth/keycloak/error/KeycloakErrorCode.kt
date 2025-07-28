package kr.pincoin.api.external.auth.keycloak.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class KeycloakErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    // 인증 관련 오류
    INVALID_CREDENTIALS(
        HttpStatus.UNAUTHORIZED,
        "잘못된 아이디 또는 비밀번호입니다",
    ),
    INVALID_CLIENT(
        HttpStatus.UNAUTHORIZED,
        "유효하지 않은 클라이언트 정보입니다",
    ),

    // 토큰 관련 오류
    INVALID_REFRESH_TOKEN(
        HttpStatus.UNAUTHORIZED,
        "유효하지 않은 리프레시 토큰입니다",
    ),
    TOKEN_REFRESH_FAILED(
        HttpStatus.UNAUTHORIZED,
        "토큰 갱신에 실패했습니다",
    ),

    // 로그아웃 관련 오류
    LOGOUT_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "로그아웃 처리 중 오류가 발생했습니다",
    ),

    // 관리자 토큰 관련 오류
    ADMIN_TOKEN_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 관리자 토큰 획득에 실패했습니다",
    ),

    // 네트워크 및 시스템 오류
    TIMEOUT(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 서버 연결 타임아웃이 발생했습니다",
    ),
    SYSTEM_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 서버 오류가 발생했습니다",
    ),
    UNKNOWN(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "Keycloak 알 수 없는 오류가 발생했습니다",
    ),
}