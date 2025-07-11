package kr.pincoin.api.external.notification.telegram.error

import kr.pincoin.api.global.exception.error.ErrorCode
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