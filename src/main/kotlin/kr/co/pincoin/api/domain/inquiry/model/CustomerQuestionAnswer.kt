package kr.co.pincoin.api.domain.inquiry.model

import java.time.ZonedDateTime

class CustomerQuestionAnswer private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 도메인 로직 불변 필드
    val questionId: Long,

    // 3. 도메인 로직 가변 필드
    content: String,
) {
    var content: String = content
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            content: String,
            questionId: Long,
        ): CustomerQuestionAnswer =
            CustomerQuestionAnswer(
                id = id,
                created = created,
                modified = modified,
                content = content,
                questionId = questionId,
            )
    }
}