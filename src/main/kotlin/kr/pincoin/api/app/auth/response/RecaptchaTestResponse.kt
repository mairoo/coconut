package kr.pincoin.api.app.auth.response

import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData

data class RecaptchaTestResponse(
    val data: RecaptchaVerifyData? = null,

    val message: String,
) {
    companion object {
        fun of(
            data: RecaptchaVerifyData,
            message: String,
        ): RecaptchaTestResponse =
            RecaptchaTestResponse(data = data, message = message)
    }
}