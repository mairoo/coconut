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
    INACTIVE(
        HttpStatus.CONFLICT,
        "사용자가 비활성화 상태입니다",
    ),
    ALREADY_EXISTS(
        HttpStatus.BAD_REQUEST,
        "아이디 또는 이메일이 이미 존재합니다",
    ),
    LOGIN_LOG_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "로그인 이력이 없습니다",
    ),
    PASSWORD_MISMATCH(
        HttpStatus.BAD_REQUEST,
        "비밀번호 불일치"
    ),
    ALREADY_DELETED(
        HttpStatus.BAD_REQUEST,
        "이미 삭제된 사용자입니다",
    ),
    EMAIL_RECENTLY_DELETED(
        HttpStatus.BAD_REQUEST,
        "최근에 삭제된 이메일 주소입니다. 30일 후에 다시 시도해주세요",
    ),
    PHONE_RECENTLY_DELETED(
        HttpStatus.BAD_REQUEST,
        "최근에 삭제된 휴대폰 번호입니다. 30일 후에 다시 시도해주세요"
    ),
    INVALID_STATUS_CHANGE(
        HttpStatus.BAD_REQUEST,
        "동일한 상태로 변경할 수 없습니다",
    ),
}