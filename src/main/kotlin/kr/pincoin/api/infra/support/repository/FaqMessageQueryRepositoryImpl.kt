package kr.pincoin.api.infra.support.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.support.entity.FaqMessageEntity
import kr.pincoin.api.infra.support.entity.QFaqMessageEntity
import org.springframework.stereotype.Repository

@Repository
class FaqMessageQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : FaqMessageQueryRepository {
    private val faqMessage = QFaqMessageEntity.faqMessageEntity

    override fun findById(
        id: Long,
    ): FaqMessageEntity? =
        queryFactory
            .selectFrom(faqMessage)
            .where(faqMessage.id.eq(id))
            .fetchOne()
}