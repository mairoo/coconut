package kr.co.pincoin.api.domain.user.model

import java.net.InetAddress
import java.time.ZonedDateTime

class LoginLog private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime,
    val modified: ZonedDateTime,

    // 2. 공통 가변 필드

    // 3. 도메인 로직 불변 필드
    val userId: Int? = null,
    val ipAddress: InetAddress,
    val email: String?,
    val username: String?,
    val userAgent: String?,
    val isSuccessful: Boolean,
    val reason: String?,

    // 4. 도메인 로직 가변 필드
) {
    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime,
            modified: ZonedDateTime,
            userId: Int? = null,
            ipAddress: InetAddress,
            email: String?,
            username: String?,
            userAgent: String?,
            isSuccessful: Boolean,
            reason: String?,
        ) = LoginLog(
            id = id,
            created = created,
            modified = modified,
            userId = userId,
            ipAddress = ipAddress,
            email = email,
            username = username,
            userAgent = userAgent,
            isSuccessful = isSuccessful,
            reason = reason,
        )
    }
}