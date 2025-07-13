package kr.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.user.entity.EmailBannedEntity
import kr.pincoin.api.infra.user.entity.QEmailBannedEntity
import org.springframework.stereotype.Repository

@Repository
class EmailBannedQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EmailBannedQueryRepository {
    private val emailBanned = QEmailBannedEntity.emailBannedEntity

    override fun findById(
        id: Long,
    ): EmailBannedEntity? =
        queryFactory
            .selectFrom(emailBanned)
            .where(emailBanned.id.eq(id))
            .fetchOne()
}