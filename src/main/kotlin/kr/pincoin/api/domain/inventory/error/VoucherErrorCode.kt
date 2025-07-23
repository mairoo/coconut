package kr.pincoin.api.domain.inventory.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class VoucherErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 상품권이 없습니다",
    ),
}