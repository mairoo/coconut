package kr.pincoin.api.external.auth.recaptcha.api.response

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Google reCAPTCHA API 응답
 */
data class RecaptchaVerifyResponse(
    @field:JsonProperty("success")
    val success: Boolean,

    @field:JsonProperty("challenge_ts")
    val challengeTs: String? = null,

    @field:JsonProperty("hostname")
    val hostname: String? = null,

    @field:JsonProperty("error-codes")
    val errorCodes: List<String>? = null,

    @field:JsonProperty("score")
    val score: Double? = null, // v3용

    @field:JsonProperty("action")
    val action: String? = null, // v3용
)