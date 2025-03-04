package kr.co.pincoin.api.domain.oauth2.model

import java.time.ZonedDateTime

class EmailConfirmation private constructor(
    // 1. 공통 불변 필드
    val id: Int? = null,
    val created: ZonedDateTime,

    // 2. 도메인 로직 불변 필드
    val key: String,
    val emailAddressId: Int,

    // 3. 도메인 로직 가변 필드
    val sent: ZonedDateTime?,
) {
    fun markAsSent(
        sentTime: ZonedDateTime = ZonedDateTime.now(),
    ): EmailConfirmation =
        copy(sent = sentTime)

    private fun copy(
        sent: ZonedDateTime? = null
    ): EmailConfirmation = EmailConfirmation(
        id = this.id,
        created = this.created,
        key = this.key,
        emailAddressId = this.emailAddressId,
        sent = sent ?: this.sent
    )

    companion object {
        fun of(
            id: Int? = null,
            created: ZonedDateTime = ZonedDateTime.now(),
            sent: ZonedDateTime? = null,
            key: String,
            emailAddressId: Int,
        ): EmailConfirmation =
            EmailConfirmation(
                id = id,
                created = created,
                sent = sent,
                key = key,
                emailAddressId = emailAddressId,
            )
    }
}