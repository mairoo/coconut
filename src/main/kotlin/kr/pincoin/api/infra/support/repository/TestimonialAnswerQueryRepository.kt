package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.TestimonialAnswerEntity

interface TestimonialAnswerQueryRepository {
    fun findById(
        id: Long,
    ): TestimonialAnswerEntity?
}