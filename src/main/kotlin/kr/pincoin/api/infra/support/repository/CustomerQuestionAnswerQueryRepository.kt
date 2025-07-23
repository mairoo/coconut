package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.infra.support.entity.CustomerQuestionAnswerEntity

interface CustomerQuestionAnswerQueryRepository {
    fun findById(
        id: Long,
    ): CustomerQuestionAnswerEntity?
}