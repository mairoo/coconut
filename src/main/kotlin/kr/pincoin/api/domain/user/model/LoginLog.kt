package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class LoginLog private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val ipAddress: String,
    val userId: Int? = null,
    val email: String? = null,
    val userAgent: String? = null,
    val isSuccessful: Boolean? = false,
    val reason: String? = null,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            ipAddress: String,
            userId: Int? = null,
            email: String?,
            userAgent: String?,
            isSuccessful: Boolean? = false,
            reason: String? = null,
        ): LoginLog {
            require(ipAddress.isNotBlank()) { "IP 주소는 필수 입력값입니다" }

            return LoginLog(
                id = id,
                created = created,
                modified = modified,
                ipAddress = ipAddress,
                userId = userId,
                email = email,
                userAgent = userAgent,
                isSuccessful = isSuccessful,
                reason = reason,
            )
        }
    }
}