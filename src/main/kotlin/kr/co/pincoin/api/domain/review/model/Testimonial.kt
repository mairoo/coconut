package kr.co.pincoin.api.domain.review.model

import java.time.ZonedDateTime

class Testimonial private constructor(
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
    val ownerId: Int?,
) {
    fun update(
        newTitle: String? = null,
        newDescription: String? = null,
        newKeywords: String? = null,
        newContent: String? = null
    ): Testimonial =
        copy(
            title = newTitle ?: title,
            description = newDescription ?: description,
            keywords = newKeywords ?: keywords,
            content = newContent ?: content
        )

    fun updateOwner(newOwnerId: Int?): Testimonial =
        copy(ownerId = newOwnerId)

    fun markAsRemoved(): Testimonial =
        copy(isRemoved = true)

    private fun copy(
        title: String? = null,
        description: String? = null,
        keywords: String? = null,
        content: String? = null,
        ownerId: Int? = this.ownerId,
        isRemoved: Boolean? = null
    ): Testimonial = Testimonial(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        title = title ?: this.title,
        description = description ?: this.description,
        keywords = keywords ?: this.keywords,
        content = content ?: this.content,
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
            ownerId: Int? = null,
        ): Testimonial =
            Testimonial(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved ?: false,
                title = title,
                description = description,
                keywords = keywords,
                content = content,
                ownerId = ownerId,
            )
    }
}