package kr.pincoin.api.domain.user.model

import java.time.LocalDateTime

class PhoneBanned private constructor(
    val id: Long? = null,
    val created: LocalDateTime = LocalDateTime.now(),
    val modified: LocalDateTime = LocalDateTime.now(),
    val isRemoved: Boolean = false,
    val phone: String
) {
    fun isActive(): Boolean = !isRemoved

    fun isInactive(): Boolean = isRemoved

    fun remove(): PhoneBanned = copy(
        isRemoved = true,
        modified = LocalDateTime.now()
    )

    fun restore(): PhoneBanned = copy(
        isRemoved = false,
        modified = LocalDateTime.now()
    )

    fun isSamePhone(phoneNumber: String): Boolean = phone == phoneNumber

    private fun copy(
        isRemoved: Boolean = this.isRemoved,
        modified: LocalDateTime = this.modified
    ): PhoneBanned = PhoneBanned(
        id = this.id,
        created = this.created,
        modified = modified,
        isRemoved = isRemoved,
        phone = this.phone
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime = LocalDateTime.now(),
            modified: LocalDateTime = LocalDateTime.now(),
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