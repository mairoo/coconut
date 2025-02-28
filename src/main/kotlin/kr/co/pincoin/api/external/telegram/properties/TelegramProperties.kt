package kr.co.pincoin.api.external.telegram.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "notification.telegram")
class TelegramProperties {
    var baseUrl: String = "https://api.telegram.org/"
    var botToken: String = ""
    var chatId: String = "-100"
}