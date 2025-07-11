package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class EmailBanned private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val email: String
) {
    fun isActive(): Boolean = !isRemoved

    fun isInactive(): Boolean = isRemoved

    fun remove(): EmailBanned {
        if (isRemoved) return this
        return copy(isRemoved = true)
    }

    fun restore(): EmailBanned {
        if (!isRemoved) return this
        return copy(isRemoved = false)
    }

    fun isSameEmail(emailAddress: String): Boolean =
        email.lowercase() == emailAddress.lowercase()

    fun getDomain(): String = email.substringAfter("@")

    fun getLocalPart(): String = email.substringBefore("@")

    fun isValidEmailFormat(): Boolean =
        email.contains("@") && email.contains(".") && email.length > 5

    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        email: String = this.email,
    ): EmailBanned = EmailBanned(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        email = email,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            email: String
        ): EmailBanned {
            require(email.isNotBlank()) { "이메일은 필수 입력값입니다" }
            require(email.contains("@")) { "올바른 이메일 형식이 아닙니다" }

            return EmailBanned(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                email = email.lowercase()
            )
        }
    }
}