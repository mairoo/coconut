package kr.pincoin.api.infra.support.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields

@Entity
@Table(name = "shop_questionanswer")
class CustomerQuestionAnswerEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "content")
    val content: String,

    @Column(name = "question_id")
    val questionId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            content: String,
            questionId: Long,
        ) = CustomerQuestionAnswerEntity(
            id = id,
            content = content,
            questionId = questionId,
        )
    }
}