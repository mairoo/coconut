package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.domain.message.model.NoticeMessage
import kr.co.pincoin.api.domain.message.repository.NoticeMessageRepository
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageRepositoryImpl(
    private val jpaRepository: NoticeMessageJpaRepository,
    private val queryRepository: NoticeMessageQueryRepository,
) : NoticeMessageRepository {
    override fun save(noticeMessage: NoticeMessage): NoticeMessage {
        TODO("Not yet implemented")
    }
}