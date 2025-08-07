package kr.pincoin.api.external.notification.aligo.api.response

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

@JsonInclude(JsonInclude.Include.NON_NULL)
data class AligoSmsResponse(
    @field:JsonProperty("result_code")
    val resultCode: String,

    @field:JsonProperty("message")
    val message: String,

    @field:JsonProperty("msg_id")
    val msgId: String,

    @field:JsonProperty("success_cnt")
    val successCount: String,

    @field:JsonProperty("error_cnt")
    val errorCount: String,

    @field:JsonProperty("msg_type")
    val msgType: String,
)