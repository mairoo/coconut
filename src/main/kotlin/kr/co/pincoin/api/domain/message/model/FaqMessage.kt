package kr.co.pincoin.api.domain.message.model

import kr.co.pincoin.api.domain.message.enums.FaqMessageCategory
import java.time.ZonedDateTime

class FaqMessage private constructor(
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
    val category: FaqMessageCategory,
    val position: Int,
    val ownerId: Int?,
) {
    fun updateBasicInfo(
        newTitle: String? = null,
        newDescription: String? = null,
        newKeywords: String? = null,
        newContent: String? = null
    ): FaqMessage = copy(
        title = newTitle ?: title,
        description = newDescription ?: description,
        keywords = newKeywords ?: keywords,
        content = newContent ?: content
    )

    fun updateCategory(newCategory: FaqMessageCategory? = null): FaqMessage = copy(
        category = newCategory ?: category
    )

    fun updatePosition(newPosition: Int? = null): FaqMessage = copy(
        position = newPosition ?: position
    )

    fun updateOwner(newOwnerId: Int?): FaqMessage = copy(
        ownerId = newOwnerId
    )

    fun markAsRemoved(): FaqMessage = copy(isRemoved = true)

    private fun copy(
        title: String? = null,
        description: String? = null,
        keywords: String? = null,
        content: String? = null,
        category: FaqMessageCategory? = null,
        position: Int? = null,
        ownerId: Int? = this.ownerId,
        isRemoved: Boolean? = null
    ): FaqMessage = FaqMessage(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        title = title ?: this.title,
        description = description ?: this.description,
        keywords = keywords ?: this.keywords,
        content = content ?: this.content,
        category = category ?: this.category,
        position = position ?: this.position,
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
            category: FaqMessageCategory,
            position: Int,
            ownerId: Int? = null,
        ): FaqMessage =
            FaqMessage(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                title = title,
                description = description,
                keywords = keywords,
                content = content,
                category = category,
                position = position,
                ownerId = ownerId,
            )
    }
}