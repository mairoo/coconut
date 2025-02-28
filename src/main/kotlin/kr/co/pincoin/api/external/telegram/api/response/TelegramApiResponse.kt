package kr.co.pincoin.api.external.telegram.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class TelegramApiResponse<T>(
    @JsonProperty("ok")
    val ok: Boolean,

    @JsonProperty("result")
    val result: T
)