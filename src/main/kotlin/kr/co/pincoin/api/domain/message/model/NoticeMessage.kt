package kr.co.pincoin.api.domain.message.model

import kr.co.pincoin.api.domain.message.enums.NoticeMessageCategory
import java.time.ZonedDateTime

class NoticeMessage private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val storeId: Long,

    // 4. 도메인 로직 가변 필드
    title: String,
    description: String,
    keywords: String,
    content: String,
    category: NoticeMessageCategory,
    ownerId: Int?,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var title: String = title
        private set

    var description: String = description
        private set

    var keywords: String = keywords
        private set

    var content: String = content
        private set

    var category: NoticeMessageCategory = category
        private set

    var ownerId: Int? = ownerId
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            title: String,
            description: String,
            keywords: String,
            content: String,
            category: NoticeMessageCategory,
            ownerId: Int? = null,
            storeId: Long,
        ): NoticeMessage =
            NoticeMessage(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                title = title,
                description = description,
                keywords = keywords,
                content = content,
                category = category,
                ownerId = ownerId,
                storeId = storeId,
            )
    }
}