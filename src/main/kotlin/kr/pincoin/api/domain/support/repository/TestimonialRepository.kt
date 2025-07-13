package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.Testimonial

interface TestimonialRepository {
    fun save(
        testimonial: Testimonial,
    ): Testimonial

    fun findById(
        id: Long,
    ): Testimonial?
}