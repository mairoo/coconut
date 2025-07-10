package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class EmailBanned private constructor(
    val id: Long? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val modified: LocalDateTime = LocalDateTime.now(),
    val isRemoved: Boolean = false,
    val email: String
) {
    fun isActive(): Boolean = !isRemoved

    fun isInactive(): Boolean = isRemoved

    fun remove(): EmailBanned = copy(
        isRemoved = true,
        modified = LocalDateTime.now()
    )

    fun restore(): EmailBanned = copy(
        isRemoved = false,
        modified = LocalDateTime.now()
    )

    fun isSameEmail(emailAddress: String): Boolean =
        email.lowercase() == emailAddress.lowercase()

    fun getDomain(): String = email.substringAfter("@")

    fun getLocalPart(): String = email.substringBefore("@")

    fun isValidEmailFormat(): Boolean =
        email.contains("@") && email.contains(".") && email.length > 5

    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        modified: LocalDateTime = this.modified
    ): EmailBanned = EmailBanned(
        id = this.id,
        created = this.created,
        modified = modified,
        isRemoved = isRemoved,
        email = this.email
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
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