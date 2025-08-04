package kr.pincoin.api.app.auth.response

import kr.pincoin.api.external.auth.recaptcha.api.response.RecaptchaVerifyData

data class RecaptchaTestResponse(
    val message: String,

    val data: RecaptchaVerifyData? = null,
)