package kr.pincoin.api.external.notification.mailgun.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class MailgunResponse(
    @field:JsonProperty("id")
    val id: String? = null,

    @field:JsonProperty("message")
    val message: String? = null,

    @field:JsonProperty("status")
    val status: Int? = null,
)