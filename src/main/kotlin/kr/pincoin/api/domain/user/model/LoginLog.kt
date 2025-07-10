package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class LoginLog private constructor(
    val id: Long? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val modified: LocalDateTime = LocalDateTime.now(),
    val ipAddress: String,
    val userId: Int? = null
) {
    fun hasUser(): Boolean = userId != null

    fun isAnonymous(): Boolean = userId == null

    fun isSameUser(targetUserId: Int): Boolean = userId == targetUserId

    fun isSameIpAddress(targetIpAddress: String): Boolean = ipAddress == targetIpAddress

    fun isRecentLogin(minutesAgo: Long = 30): Boolean {
        val cutoffTime = LocalDateTime.now().minusMinutes(minutesAgo)
        return created.isAfter(cutoffTime)
    }

    fun isOldLogin(daysAgo: Long = 30): Boolean {
        val cutoffTime = LocalDateTime.now().minusDays(daysAgo)
        return created.isBefore(cutoffTime)
    }

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
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