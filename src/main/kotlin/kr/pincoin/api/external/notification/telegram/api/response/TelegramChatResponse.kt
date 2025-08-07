package kr.pincoin.api.external.notification.telegram.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TelegramChatResponse(
    @field:JsonProperty("id")
    val id: Long,

    @field:JsonProperty("title")
    val title: String,

    @field:JsonProperty("type")
    val type: String,
)