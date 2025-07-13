package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.EmailConfirmationEntity
import kr.pincoin.api.infra.social.entity.QEmailConfirmationEntity
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EmailConfirmationQueryRepository {
    private val emailConfirmation = QEmailConfirmationEntity.emailConfirmationEntity

    override fun findById(
        id: Int,
    ): EmailConfirmationEntity? =
        queryFactory
            .selectFrom(emailConfirmation)
            .where(emailConfirmation.id.eq(id))
            .fetchOne()
}