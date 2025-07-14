package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class AuthToken private constructor(
    val key: String,
    val userId: Int,
    val created: LocalDateTime? = null,
) {
    private fun copy(
        key: String = this.key,
        userId: Int = this.userId,
        created: LocalDateTime? = this.created,
    ): AuthToken = AuthToken(
        key = key,
        userId = userId,
        created = created,
    )

    companion object {
        fun of(
            key: String,
            userId: Int,
            created: LocalDateTime? = null,
        ): AuthToken = AuthToken(
            key = key,
            userId = userId,
            created = created,
        )
    }
}