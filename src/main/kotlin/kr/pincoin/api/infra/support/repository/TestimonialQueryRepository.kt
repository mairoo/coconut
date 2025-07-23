package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.TestimonialEntity

interface TestimonialQueryRepository {
    fun findById(
        id: Long,
    ): TestimonialEntity?
}