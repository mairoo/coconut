package kr.pincoin.api.domain.support.model

import java.time.LocalDateTime

class ShortMessage private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val phoneFrom: String? = null,
    val phoneTo: String? = null,
    val content: String,
    val success: Boolean = false,
) {
    private fun copy(
        phoneFrom: String? = this.phoneFrom,
        phoneTo: String? = this.phoneTo,
        content: String = this.content,
        success: Boolean = this.success,
    ): ShortMessage = ShortMessage(
        id = this.id,
        created = this.created,
        modified = this.modified,
        phoneFrom = phoneFrom,
        phoneTo = phoneTo,
        content = content,
        success = success,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            phoneFrom: String? = null,
            phoneTo: String? = null,
            content: String,
            success: Boolean = false,
        ): ShortMessage = ShortMessage(
            id = id,
            created = created,
            modified = modified,
            phoneFrom = phoneFrom,
            phoneTo = phoneTo,
            content = content,
            success = success,
        )
    }
}