package kr.pincoin.api.domain.social.model

class EmailAddress private constructor(
    val id: Int? = null,
    val email: String,
    val verified: Boolean = false,
    val primary: Boolean = false,
    val userId: Int
) {
    fun verify(): EmailAddress =
        copy(verified = true)

    fun unverify(): EmailAddress =
        copy(verified = false)

    fun setPrimary(): EmailAddress =
        copy(primary = true)

    fun removePrimary(): EmailAddress =
        copy(primary = false)

    fun updateEmail(newEmail: String): EmailAddress =
        copy(email = newEmail.lowercase(), verified = false)

    fun isVerified(): Boolean = verified

    fun isPrimary(): Boolean = primary

    fun isActive(): Boolean = verified && !email.isBlank()

    fun isSameEmail(targetEmail: String): Boolean =
        email.lowercase() == targetEmail.lowercase()

    fun belongsToUser(targetUserId: Int): Boolean =
        userId == targetUserId

    fun getDomain(): String = email.substringAfter("@")

    fun getLocalPart(): String = email.substringBefore("@")

    fun isValidEmailFormat(): Boolean =
        email.contains("@") && email.contains(".") && email.length > 5

    private fun copy(
        email: String = this.email,
        verified: Boolean = this.verified,
        primary: Boolean = this.primary,
        userId: Int = this.userId
    ): EmailAddress = EmailAddress(
        id = this.id,
        email = email,
        verified = verified,
        primary = primary,
        userId = userId
    )

    companion object {
        fun of(
            id: Int? = null,
            email: String,
            verified: Boolean = false,
            primary: Boolean = false,
            userId: Int
        ): EmailAddress {
            require(email.isNotBlank()) { "이메일은 필수 입력값입니다" }
            require(email.contains("@")) { "올바른 이메일 형식이 아닙니다" }
            require(userId > 0) { "사용자 ID는 양수여야 합니다" }

            return EmailAddress(
                id = id,
                email = email.lowercase(),
                verified = verified,
                primary = primary,
                userId = userId
            )
        }
    }
}