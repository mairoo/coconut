package kr.co.pincoin.api.global.exception

import kr.co.pincoin.api.global.exception.code.ErrorCode

class JwtAuthenticationException(
    private val errorCode: ErrorCode,
    override val message: String = errorCode.message
) : RuntimeException(message)