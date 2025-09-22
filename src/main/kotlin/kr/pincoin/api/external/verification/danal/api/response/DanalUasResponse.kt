package kr.pincoin.api.external.verification.danal.api.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DanalUasResponse(
    @field:JsonProperty("RETURNCODE")
    val returnCode: String,

    @field:JsonProperty("RETURNMSG")
    val returnMessage: String? = null,

    @field:JsonProperty("TID")
    val tid: String? = null,
)