package kr.pincoin.api.external.notification.telegram.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TelegramApiResponse<T>(
    @field:JsonProperty("ok")
    val ok: Boolean,

    @field:JsonProperty("result")
    val result: T,
)