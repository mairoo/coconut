package kr.pincoin.api.domain.support.model

import java.time.LocalDateTime

class TestimonialAnswer private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val content: String,
    val testimonialId: Long,
) {
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