package kr.pincoin.api.external.notification.smtp.api.response

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class SmtpResponse(
    val success: Boolean = true,
    val message: String? = null,
    val messageId: String? = null,
)
