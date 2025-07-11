package kr.pincoin.api.domain.user.event

data class LoginEvent(
    val ipAddress: String,
    val userId: Int? = null,
    val email: String?,
    val userAgent: String?,
    val isSuccessful: Boolean? = false,
    val reason: String? = null,
)