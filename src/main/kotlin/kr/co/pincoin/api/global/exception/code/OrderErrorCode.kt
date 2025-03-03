package kr.co.pincoin.api.global.exception.code

import org.springframework.http.HttpStatus

enum class OrderErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    ORDER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 주문이 없습니다",
    ),
    ORDER_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "주문 저장에 실패했습니다",
    ),
    ORDER_PRODUCT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 주문상품이 없습니다",
    ),
    ORDER_PRODUCT_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "주문상품 저장에 실패했습니다",
    ),
    ORDER_PAYMENT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 주문결제가 없습니다",
    ),
    ORDER_PAYMENT_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "주문결제 저장에 실패했습니다",
    ),
    ORDER_PRODUCT_VOUCHER_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 주문발송상품권이 없습니다",
    ),
    ORDER_PRODUCT_VOUCHER_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "주문발송상품권 저장에 실패했습니다",
    ),
}