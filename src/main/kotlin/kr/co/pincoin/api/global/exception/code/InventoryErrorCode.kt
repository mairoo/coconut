package kr.co.pincoin.api.global.exception.code

import org.springframework.http.HttpStatus

enum class InventoryErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    VOUCHER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 카테고리가 없습니다",
    ),
    VOUCHER_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "상품권 코드가 이미 존재합니다",
    ),
    VOUCHER_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "상품권 코드 저장에 실패했습니다",
    ),
}