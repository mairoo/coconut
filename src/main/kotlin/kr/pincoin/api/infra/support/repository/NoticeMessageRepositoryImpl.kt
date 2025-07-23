package kr.pincoin.api.infra.support.repository

import kr.pincoin.api.domain.support.model.NoticeMessage
import kr.pincoin.api.domain.support.repository.NoticeMessageRepository
import kr.pincoin.api.infra.support.mapper.toEntity
import kr.pincoin.api.infra.support.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageRepositoryImpl(
    private val jpaRepository: NoticeMessageJpaRepository,
    private val queryRepository: NoticeMessageQueryRepository,
) : NoticeMessageRepository {
    override fun save(
        noticeMessage: NoticeMessage,
    ): NoticeMessage =
        noticeMessage.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("공지사항 저장 실패")

    override fun findById(id: Long): NoticeMessage? =
        queryRepository.findById(id)?.toModel()
}