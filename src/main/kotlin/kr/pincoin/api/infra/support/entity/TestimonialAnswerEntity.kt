package kr.pincoin.api.infra.support.entity

import jakarta.persistence.*
import kr.pincoin.api.infra.common.jpa.DateTimeFields

@Entity
@Table(name = "shop_testimonialsanswer")
class TestimonialAnswerEntity private constructor(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    val id: Long?,

    @Embedded
    val dateTimeFields: DateTimeFields = DateTimeFields(),

    @Column(name = "content")
    val content: String,

    @Column(name = "testimonial_id")
    val testimonialId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            content: String,
            testimonialId: Long,
        ) = TestimonialAnswerEntity(
            id = id,
            content = content,
            testimonialId = testimonialId,
        )
    }
}