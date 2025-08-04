package kr.pincoin.api.app.auth.response

data class RecaptchaStatusResponse(
    val enabled: Boolean,

    val message: String,
)