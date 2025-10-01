package kr.pincoin.api.external.notification.smtp.service

import jakarta.mail.internet.MimeMessage
import kr.pincoin.api.external.notification.smtp.api.request.SmtpRequest
import kr.pincoin.api.external.notification.smtp.api.response.SmtpResponse
import kr.pincoin.api.external.notification.smtp.error.SmtpErrorCode
import kr.pincoin.api.external.notification.smtp.properties.SmtpProperties
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.mail.MailAuthenticationException
import org.springframework.mail.MailSendException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers

@Component
class SmtpEmailClient(
    private val smtpMailSender: JavaMailSender,
    private val smtpProperties: SmtpProperties,
) {
    fun sendEmail(request: SmtpRequest): Mono<SmtpResponse> {
        return Mono.fromCallable {
            try {
                val message: MimeMessage = smtpMailSender.createMimeMessage()
                val helper = MimeMessageHelper(message, true, "UTF-8")

                helper.setFrom(smtpProperties.from)
                helper.setTo(request.to)
                helper.setSubject(request.subject)

                if (request.html != null) {
                    helper.setText(request.text, request.html)
                } else {
                    helper.setText(request.text, false)
                }

                smtpMailSender.send(message)

                SmtpResponse(
                    success = true,
                    message = "이메일이 성공적으로 발송되었습니다",
                    messageId = message.messageID
                )
            } catch (_: MailAuthenticationException) {
                throw BusinessException(SmtpErrorCode.SMTP_AUTH_ERROR)
            } catch (_: MailSendException) {
                throw BusinessException(SmtpErrorCode.SMTP_CONNECTION_ERROR)
            } catch (_: Exception) {
                throw BusinessException(SmtpErrorCode.SMTP_SEND_ERROR)
            }
        }.subscribeOn(Schedulers.boundedElastic())
    }
}