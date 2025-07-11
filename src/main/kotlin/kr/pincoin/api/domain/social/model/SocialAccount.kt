package kr.pincoin.api.domain.social.model

import java.time.LocalDateTime

class SocialAccount private constructor(
    val id: Int? = null,
    val provider: String,
    val uid: String,
    val lastLogin: LocalDateTime,
    val dateJoined: LocalDateTime,
    val extraData: String,
    val userId: Int
) {
    fun updateLastLogin(): SocialAccount =
        copy(lastLogin = LocalDateTime.now())

    fun updateExtraData(newExtraData: String): SocialAccount =
        copy(extraData = newExtraData)

    fun isActive(): Boolean =
        uid.isNotBlank() && provider.isNotBlank()

    fun isSameProvider(targetProvider: String): Boolean =
        provider.equals(targetProvider, ignoreCase = true)

    fun isSameUid(targetUid: String): Boolean =
        uid == targetUid

    fun belongsToUser(targetUserId: Int): Boolean =
        userId == targetUserId

    private fun copy(
        provider: String = this.provider,
        uid: String = this.uid,
        lastLogin: LocalDateTime = this.lastLogin,
        dateJoined: LocalDateTime = this.dateJoined,
        extraData: String = this.extraData,
        userId: Int = this.userId
    ): SocialAccount = SocialAccount(
        id = this.id,
        provider = provider,
        uid = uid,
        lastLogin = lastLogin,
        dateJoined = dateJoined,
        extraData = extraData,
        userId = userId
    )

    companion object {
        fun of(
            id: Int? = null,
            provider: String,
            uid: String,
            lastLogin: LocalDateTime = LocalDateTime.now(),
            dateJoined: LocalDateTime = LocalDateTime.now(),
            extraData: String = "",
            userId: Int
        ): SocialAccount {
            require(provider.isNotBlank()) { "프로바이더는 필수 입력값입니다" }
            require(uid.isNotBlank()) { "UID는 필수 입력값입니다" }
            require(userId > 0) { "사용자 ID는 양수여야 합니다" }

            return SocialAccount(
                id = id,
                provider = provider,
                uid = uid,
                lastLogin = lastLogin,
                dateJoined = dateJoined,
                extraData = extraData,
                userId = userId
            )
        }
    }
}