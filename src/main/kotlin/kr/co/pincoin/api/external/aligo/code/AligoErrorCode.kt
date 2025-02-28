package kr.co.pincoin.api.external.aligo.code

import kr.co.pincoin.api.global.exception.code.ErrorCode
import org.springframework.http.HttpStatus

enum class AligoErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    ALIGO_API_PARSE_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "알리고 API 응답 파싱 실패"
    ),
    ALIGO_API_SEND_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "알리고 SMS 발송 실패"
    ),
}