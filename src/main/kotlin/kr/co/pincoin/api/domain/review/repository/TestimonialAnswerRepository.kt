package kr.co.pincoin.api.domain.review.repository

import kr.co.pincoin.api.domain.review.model.TestimonialAnswer

interface TestimonialAnswerRepository {
    fun save(
        testimonialAnswer: TestimonialAnswer,
    ): TestimonialAnswer
}