package kr.co.pincoin.api.external.mailgun.code

import kr.co.pincoin.api.global.exception.code.ErrorCode
import org.springframework.http.HttpStatus

enum class MailgunErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    MAILGUN_API_PARSE_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "mailgun API 응답 파싱 실패"
    ),
    MAILGUN_API_SEND_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "mailgun 이메일 발송 실패"
    ),
}