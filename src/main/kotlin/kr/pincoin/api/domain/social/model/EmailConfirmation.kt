package kr.pincoin.api.domain.social.model

import java.time.LocalDateTime

class EmailConfirmation private constructor(
    val id: Int? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val sent: LocalDateTime? = null,
    val key: String,
    val emailAddressId: Int
) {
    fun markAsSent(): EmailConfirmation =
        copy(sent = LocalDateTime.now())

    fun isExpired(validityHours: Long = 24): Boolean =
        created.isBefore(LocalDateTime.now().minusHours(validityHours))

    fun isSent(): Boolean =
        sent != null

    fun isValid(): Boolean =
        !isExpired() && key.isNotBlank()

    fun canResend(cooldownMinutes: Long = 5): Boolean =
        sent?.isBefore(LocalDateTime.now().minusMinutes(cooldownMinutes)) ?: true

    private fun copy(
        created: LocalDateTime = this.created,
        sent: LocalDateTime? = this.sent,
        key: String = this.key,
        emailAddressId: Int = this.emailAddressId
    ): EmailConfirmation = EmailConfirmation(
        id = this.id,
        created = created,
        sent = sent,
        key = key,
        emailAddressId = emailAddressId
    )

    companion object {
        fun of(
            id: Int? = null,
            created: LocalDateTime = LocalDateTime.now(),
            sent: LocalDateTime? = null,
            key: String,
            emailAddressId: Int
        ): EmailConfirmation {
            require(key.isNotBlank()) { "확인 키는 필수 입력값입니다" }
            require(emailAddressId > 0) { "이메일 주소 ID는 양수여야 합니다" }

            return EmailConfirmation(
                id = id,
                created = created,
                sent = sent,
                key = key,
                emailAddressId = emailAddressId
            )
        }

        fun generateKey(): String {
            return java.util.UUID.randomUUID().toString().replace("-", "")
        }
    }
}