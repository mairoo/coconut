package kr.co.pincoin.api.infra.inquiry.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields

@Entity
@Table(name = "shop_questionanswer")
class CustomerQuestionAnswerEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "content", columnDefinition = "text")
    val content: String,

    @Column(name = "question_id")
    val questionId: Long,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            content: String,
            questionId: Long
        ) = CustomerQuestionAnswerEntity(
            id = id,
            content = content,
            questionId = questionId
        )
    }
}