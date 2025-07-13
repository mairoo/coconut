package kr.pincoin.api.domain.support.repository

import kr.pincoin.api.domain.support.model.NoticeMessage

interface NoticeMessageRepository {
    fun save(
        noticeMessage: NoticeMessage,
    ): NoticeMessage

    fun findById(
        id: Long,
    ): NoticeMessage?
}