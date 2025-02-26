package kr.co.pincoin.api.domain.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestionAnswer

interface CustomerQuestionAnswerRepository {
    fun save(
        customerQuestionAnswer: CustomerQuestionAnswer,
    ): CustomerQuestionAnswer
}