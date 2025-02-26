package kr.co.pincoin.api.infra.message.repository

import kr.co.pincoin.api.domain.message.model.NoticeMessage
import kr.co.pincoin.api.domain.message.repository.NoticeMessageRepository
import kr.co.pincoin.api.infra.message.mapper.toEntity
import kr.co.pincoin.api.infra.message.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageRepositoryImpl(
    private val jpaRepository: NoticeMessageJpaRepository,
    private val queryRepository: NoticeMessageQueryRepository,
) : NoticeMessageRepository {
    override fun save(noticeMessage: NoticeMessage): NoticeMessage =
        noticeMessage.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("공지사항 저장 실패")
}