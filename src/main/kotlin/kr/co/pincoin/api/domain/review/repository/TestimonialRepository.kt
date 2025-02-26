package kr.co.pincoin.api.domain.review.repository

import kr.co.pincoin.api.domain.review.model.Testimonial

interface TestimonialRepository {
    fun save(
        testimonial: Testimonial,
    ): Testimonial
}