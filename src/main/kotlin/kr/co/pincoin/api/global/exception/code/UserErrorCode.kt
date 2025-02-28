package kr.co.pincoin.api.global.exception.code

import org.springframework.http.HttpStatus

enum class UserErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    USER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 사용자가 없습니다",
    ),
    USER_INACTIVE(
        HttpStatus.BAD_REQUEST,
        "사용자가 비활성화 상태입니다",
    ),
    USER_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "아이디 또는 이메일이 이미 존재합니다",
    ),
    SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "사용자 정보 저장에 실패했습니다",
    ),
    LOGIN_LOG_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "로그인 이력이 없습니다",
    ),
}