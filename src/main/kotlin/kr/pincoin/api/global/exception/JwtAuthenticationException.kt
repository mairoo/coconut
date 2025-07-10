package kr.pincoin.api.global.exception

import kr.pincoin.api.global.exception.error.ErrorCode

class JwtAuthenticationException(
    private val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)