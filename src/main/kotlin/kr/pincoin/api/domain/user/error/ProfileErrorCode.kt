package kr.pincoin.api.domain.user.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ProfileErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 프로필이 없습니다",
    ),
    ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "이미 존재하는 프로필입니다",
    ),
}