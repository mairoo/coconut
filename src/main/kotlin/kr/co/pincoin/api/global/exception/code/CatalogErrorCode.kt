package kr.co.pincoin.api.global.exception.code

import org.springframework.http.HttpStatus

enum class CatalogErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    CATEGORY_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 카테고리가 없습니다",
    ),
    CATEGORY_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "카테고리가 이미 존재합니다",
    ),
    CATEGORY_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "카테고리 저장에 실패했습니다",
    ),
    PRODUCT_NOT_FOUND(
        HttpStatus.NOT_FOUND,
        "유효한 상품이 없습니다",
    ),
    PRODUCT_ALREADY_EXISTS(
        HttpStatus.CONFLICT,
        "상품이 이미 존재합니다",
    ),
    PRODUCT_SAVE_FAILED(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "상품 저장에 실패했습니다",
    ),
}