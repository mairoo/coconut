package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class LoginLog private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val ipAddress: String,
    val userId: Int? = null
) {
    fun hasUser(): Boolean = userId != null

    fun isAnonymous(): Boolean = userId == null

    fun isSameUser(targetUserId: Int): Boolean = userId == targetUserId

    fun isSameIpAddress(targetIpAddress: String): Boolean = ipAddress == targetIpAddress

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            ipAddress: String,
            userId: Int? = null
        ): LoginLog {
            require(ipAddress.isNotBlank()) { "IP 주소는 필수 입력값입니다" }

            return LoginLog(
                id = id,
                created = created,
                modified = modified,
                ipAddress = ipAddress,
                userId = userId
            )
        }
    }
}