package kr.co.pincoin.api.domain.message.repository

import kr.co.pincoin.api.domain.message.model.FaqMessage

interface FaqMessageRepository {
    fun save(
        faqMessage: FaqMessage,
    ): FaqMessage
}