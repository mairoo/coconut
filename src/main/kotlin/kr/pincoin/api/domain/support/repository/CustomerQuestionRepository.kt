package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.CustomerQuestion

interface CustomerQuestionRepository {
    fun save(
        customerQuestion: CustomerQuestion,
    ): CustomerQuestion

    fun findById(
        id: Long,
    ): CustomerQuestion?
}