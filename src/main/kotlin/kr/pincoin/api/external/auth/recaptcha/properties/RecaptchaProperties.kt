package kr.pincoin.api.external.auth.recaptcha.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "recaptcha")
data class RecaptchaProperties(
    val v2: RecaptchaV2Config = RecaptchaV2Config(),
    val v3: RecaptchaV3Config = RecaptchaV3Config(),
    val verifyUrl: String = "https://www.google.com/recaptcha/api/siteverify",
    val timeout: Long = 5000,
    val enabled: Boolean = true,
    val minScore: Double = 0.5, // v3용 기본 최소 점수
)

data class RecaptchaV2Config(
    val siteKey: String = "",
    val secretKey: String = "",
)

data class RecaptchaV3Config(
    val siteKey: String = "",
    val secretKey: String = "",
)
