package kr.pincoin.api.external.notification.smtp.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification.smtp")
class SmtpProperties {
    var host: String = "smtp.mailgun.org" // gmail:  smtp.gmail.com
    var port: Int = 587
    var username: String = "postmaster@mg.example.com" // gmail: email address
    var password: String = "" // gmail: app password
    var from: String = "고객센터 <help@example.com>"
    var enableSsl: Boolean = false
    var enableStartTls: Boolean = true
    var enableAuth: Boolean = true
}