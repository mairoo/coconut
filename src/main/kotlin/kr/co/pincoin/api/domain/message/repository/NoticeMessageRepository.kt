package kr.co.pincoin.api.domain.message.repository

import kr.co.pincoin.api.domain.message.model.NoticeMessage

interface NoticeMessageRepository {
    fun save(
        noticeMessage: NoticeMessage,
    ): NoticeMessage
}