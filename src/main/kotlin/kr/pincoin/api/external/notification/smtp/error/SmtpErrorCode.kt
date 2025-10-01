package kr.pincoin.api.external.notification.smtp.error

import kr.pincoin.api.global.error.ErrorCode
import org.springframework.http.HttpStatus

enum class SmtpErrorCode(
    override val status: HttpStatus,
    override val message: String,
) : ErrorCode {
    SMTP_SEND_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "SMTP 이메일 발송 실패"
    ),
    SMTP_AUTH_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "SMTP 인증 실패"
    ),
    SMTP_CONNECTION_ERROR(
        HttpStatus.INTERNAL_SERVER_ERROR,
        "SMTP 서버 연결 실패"
    ),
}
