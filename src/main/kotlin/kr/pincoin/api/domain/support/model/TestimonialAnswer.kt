package kr.pincoin.api.domain.support.model

import java.time.LocalDateTime

class TestimonialAnswer private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val content: String,
    val testimonialId: Long,
) {
    private fun copy(
        content: String = this.content,
        testimonialId: Long = this.testimonialId,
    ): TestimonialAnswer = TestimonialAnswer(
        id = this.id,
        created = this.created,
        modified = this.modified,
        content = content,
        testimonialId = testimonialId,
    )

    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            content: String,
            testimonialId: Long,
        ): TestimonialAnswer = TestimonialAnswer(
            id = id,
            created = created,
            modified = modified,
            content = content,
            testimonialId = testimonialId,
        )
    }
}