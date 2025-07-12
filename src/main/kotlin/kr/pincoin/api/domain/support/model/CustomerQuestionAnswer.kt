package kr.pincoin.api.domain.support.model

import java.time.LocalDateTime

class CustomerQuestionAnswer private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val content: String,
    val questionId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            content: String,
            questionId: Long,
        ): CustomerQuestionAnswer = CustomerQuestionAnswer(
            id = id,
            created = created,
            modified = modified,
            content = content,
            questionId = questionId,
        )
    }
}