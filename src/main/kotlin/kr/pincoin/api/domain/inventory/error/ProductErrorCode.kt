package kr.pincoin.api.domain.inventory.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class ProductErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 카테고리가 없습니다",
    ),
    DISABLED(
        HttpStatus.BAD_REQUEST,
        "비활성화된 상품입니다",
    ),
    OUT_OF_STOCK(
        HttpStatus.BAD_REQUEST,
        "재고가 없는 상품입니다",
    ),
}