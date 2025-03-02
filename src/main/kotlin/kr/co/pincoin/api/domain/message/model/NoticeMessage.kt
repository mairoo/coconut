package kr.co.pincoin.api.domain.message.model

import kr.co.pincoin.api.domain.message.enums.NoticeMessageCategory
import java.time.ZonedDateTime

class NoticeMessage private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    // 4. 도메인 로직 가변 필드
    val title: String,
    val description: String,
    val keywords: String,
    val content: String,
    val category: NoticeMessageCategory,
    val ownerId: Int?,
) {
    fun updateBasicInfo(
        newTitle: String? = null,
        newDescription: String? = null,
        newKeywords: String? = null,
        newContent: String? = null
    ): NoticeMessage = copy(
        title = newTitle ?: title,
        description = newDescription ?: description,
        keywords = newKeywords ?: keywords,
        content = newContent ?: content
    )

    fun updateCategory(newCategory: NoticeMessageCategory? = null): NoticeMessage = copy(
        category = newCategory ?: category
    )

    fun updateOwner(newOwnerId: Int?): NoticeMessage = copy(
        ownerId = newOwnerId
    )

    fun markAsRemoved(): NoticeMessage = copy(isRemoved = true)

    private fun copy(
        title: String? = null,
        description: String? = null,
        keywords: String? = null,
        content: String? = null,
        category: NoticeMessageCategory? = null,
        ownerId: Int? = this.ownerId,
        isRemoved: Boolean? = null
    ): NoticeMessage = NoticeMessage(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        title = title ?: this.title,
        description = description ?: this.description,
        keywords = keywords ?: this.keywords,
        content = content ?: this.content,
        category = category ?: this.category,
        ownerId = ownerId
    )

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
        ): NoticeMessage =
            NoticeMessage(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                title = title,
                description = description,
                keywords = keywords,
                content = content,
                category = category,
                ownerId = ownerId,
            )
    }
}