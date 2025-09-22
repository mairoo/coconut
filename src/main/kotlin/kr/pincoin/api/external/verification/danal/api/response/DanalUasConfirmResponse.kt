package kr.pincoin.api.external.verification.danal.api.response

import com.fasterxml.jackson.annotation.JsonProperty

data class DanalUasConfirmResponse(
    @field:JsonProperty("RETURNCODE")
    val returnCode: String,

    @field:JsonProperty("RETURNMSG")
    val returnMessage: String,

    @field:JsonProperty("TID")
    val tid: String,

    @field:JsonProperty("CI")
    val ci: String,

    @field:JsonProperty("DI")
    val di: String,

    @field:JsonProperty("ORDERID")
    val orderId: String? = null,

    @field:JsonProperty("NAME")
    val name: String,

    @field:JsonProperty("USERID")
    val userId: String? = null,

    @field:JsonProperty("IDEN")
    val iden: String? = null, // IDENOPTION이 0일 경우 (생년월일+성별, ex: 1401011)

    @field:JsonProperty("DOB")
    val dob: String? = null, // IDENOPTION이 1일 경우 (생년월일, ex: 20140101)

    @field:JsonProperty("SEX")
    val sex: String? = null, // IDENOPTION이 1일 경우 (성별, ex: 1)

    @field:JsonProperty("PHONE")
    val phone: String? = null, // 휴대폰 번호 (선택사항)

    @field:JsonProperty("TELECOM")
    val telecom: String? = null, // 통신사 코드 (선택사항)
)