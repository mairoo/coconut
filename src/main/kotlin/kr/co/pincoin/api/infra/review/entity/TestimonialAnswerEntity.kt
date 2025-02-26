package kr.co.pincoin.api.infra.review.entity

import jakarta.persistence.*
import kr.co.pincoin.api.infra.common.jpa.DateTimeFields

@Entity
@Table(name = "shop_testimonialsanswer")
class TestimonialAnswerEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long? = null,

    @Column(name = "content", columnDefinition = "text")
    val content: String,

    @Column(name = "testimonial_id")
    val testimonialId: Long,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),
) {
    companion object {
        fun of(
            id: Long? = null,
            content: String,
            testimonialId: Long
        ) = TestimonialAnswerEntity(
            id = id,
            content = content,
            testimonialId = testimonialId
        )
    }
}