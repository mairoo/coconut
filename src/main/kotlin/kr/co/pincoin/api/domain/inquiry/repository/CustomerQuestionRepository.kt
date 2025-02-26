package kr.co.pincoin.api.domain.inquiry.repository

import kr.co.pincoin.api.domain.inquiry.model.CustomerQuestion

interface CustomerQuestionRepository {
    fun save(
        customerQuestion: CustomerQuestion,
    ): CustomerQuestion
}