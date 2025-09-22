package kr.pincoin.api.domain.order.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class OrderPaymentErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 주문결제가 없습니다",
    ),
    ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "이미 존재하는 주문결제입니다",
    ),
}