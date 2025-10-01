package kr.pincoin.api.external.notification.smtp.config

import kr.pincoin.api.external.notification.smtp.properties.SmtpProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.JavaMailSenderImpl
import java.util.*

@Configuration
class SmtpMailSenderConfig(
    private val smtpProperties: SmtpProperties,
) {
    @Bean
    fun smtpMailSender(): JavaMailSender {
        val mailSender = JavaMailSenderImpl()

        mailSender.host = smtpProperties.host
        mailSender.port = smtpProperties.port
        mailSender.username = smtpProperties.username
        mailSender.password = smtpProperties.password

        val props: Properties = mailSender.javaMailProperties
        props["mail.transport.protocol"] = "smtp"
        props["mail.smtp.auth"] = smtpProperties.enableAuth.toString()
        props["mail.smtp.starttls.enable"] = smtpProperties.enableStartTls.toString()
        props["mail.smtp.ssl.enable"] = smtpProperties.enableSsl.toString()
        props["mail.debug"] = "false"

        return mailSender
    }
}
