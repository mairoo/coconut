package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.CustomerQuestionEntity

interface CustomerQuestionQueryRepository {
    fun findById(
        id: Long,
    ): CustomerQuestionEntity?
}