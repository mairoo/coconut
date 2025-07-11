package kr.pincoin.api.domain.user.event

data class LoginEvent(
    val ipAddress: String,
    val userId: Int? = null,
)