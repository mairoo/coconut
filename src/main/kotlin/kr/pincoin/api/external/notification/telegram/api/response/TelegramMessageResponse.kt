package kr.pincoin.api.external.notification.telegram.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TelegramMessageResponse(
    @field:JsonProperty("message_id")
    val messageId: Long,

    @field:JsonProperty("sender_chat")
    val senderChat: TelegramChatResponse,

    @field:JsonProperty("chat")
    val chat: TelegramChatResponse,

    @field:JsonProperty("date")
    val date: Long,

    @field:JsonProperty("text")
    val text: String,
)