package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class PhoneBanned private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val phone: String
) {
    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        phone: String = this.phone,
    ): PhoneBanned = PhoneBanned(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved,
        phone = phone,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            phone: String
        ): PhoneBanned {
            require(phone.isNotBlank()) { "전화번호는 필수 입력값입니다" }

            return PhoneBanned(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                phone = phone
            )
        }
    }
}