package kr.co.pincoin.api.external.telegram.code

import kr.co.pincoin.api.global.exception.code.ErrorCode
import org.springframework.http.HttpStatus

enum class TelegramErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    TELEGRAM_API_PARSE_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "텔레그램 API 응답 파싱 실패"
    ),
    TELEGRAM_API_SEND_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "텔레그램 메시지 발송 실패"
    ),
}