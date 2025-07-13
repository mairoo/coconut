package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.FaqMessage

interface FaqMessageRepository {
    fun save(
        faqMessage: FaqMessage,
    ): FaqMessage

    fun findById(
        id: Long,
    ): FaqMessage?
}