package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.TestimonialAnswer

interface TestimonialAnswerRepository {
    fun save(
        testimonialAnswer: TestimonialAnswer,
    ): TestimonialAnswer

    fun findById(
        id: Long,
    ): TestimonialAnswer?
}