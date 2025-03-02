package kr.co.pincoin.api.domain.review.model

import java.time.ZonedDateTime

class TestimonialAnswer private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 도메인 로직 불변 필드
    val testimonialId: Long,

    // 3. 도메인 로직 가변 필드
    val content: String,
) {
    fun updateContent(newContent: String? = null): TestimonialAnswer = copy(
        content = newContent ?: content
    )

    private fun copy(
        content: String? = null
    ): TestimonialAnswer = TestimonialAnswer(
        id = this.id,
        created = this.created,
        modified = this.modified,
        testimonialId = this.testimonialId,
        content = content ?: this.content
    )

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            content: String,
            testimonialId: Long,
        ): TestimonialAnswer =
            TestimonialAnswer(
                id = id,
                created = created,
                modified = modified,
                content = content,
                testimonialId = testimonialId,
            )
    }
}