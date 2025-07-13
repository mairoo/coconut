package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.CustomerQuestionAnswer

interface CustomerQuestionAnswerRepository {
    fun save(
        customerQuestionAnswer: CustomerQuestionAnswer,
    ): CustomerQuestionAnswer

    fun findById(
        id: Long,
    ): CustomerQuestionAnswer?
}