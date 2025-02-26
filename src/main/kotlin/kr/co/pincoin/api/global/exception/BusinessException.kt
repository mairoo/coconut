package kr.co.pincoin.api.global.exception

import kr.co.pincoin.api.global.exception.code.ErrorCode

class BusinessException(
    val errorCode: ErrorCode,
    message: String? = errorCode.message,
    cause: Throwable? = null
) : RuntimeException(message, cause)