package kr.pincoin.api.external.verification.danal.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "danal")
data class DanalProperties(
    val baseUrl: String = "https://uas.teledit.com",
    val cpId: String = "",
    val cpPwd: String = "",
    val targetUrl: String = "",
    val cpTitle: String = "",
    val timeout: Long = 30000,
    val enabled: Boolean = true,
)