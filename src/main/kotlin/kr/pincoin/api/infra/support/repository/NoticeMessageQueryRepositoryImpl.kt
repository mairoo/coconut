package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.NoticeMessageEntity
import kr.pincoin.api.infra.support.entity.QNoticeMessageEntity
import org.springframework.stereotype.Repository

@Repository
class NoticeMessageQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : NoticeMessageQueryRepository {
    private val noticeMessage = QNoticeMessageEntity.noticeMessageEntity

    override fun findById(
        id: Long,
    ): NoticeMessageEntity? =
        queryFactory
            .selectFrom(noticeMessage)
            .where(noticeMessage.id.eq(id))
            .fetchOne()
}