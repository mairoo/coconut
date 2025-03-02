package kr.co.pincoin.api.domain.user.event

import java.time.ZonedDateTime

data class LoginEvent(
    val ipAddress: String,
    val email: String?,
    val username: String?,
    val userAgent: String?,
    val isSuccessful: Boolean,
    val reason: String? = null,
    val userId: Int? = null,
    val timestamp: ZonedDateTime = ZonedDateTime.now()
)