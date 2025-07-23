package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.EmailAddressEntity
import kr.pincoin.api.infra.social.entity.QEmailAddressEntity
import org.springframework.stereotype.Repository

@Repository
class EmailAddressQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EmailAddressQueryRepository {
    private val emailAddress = QEmailAddressEntity.emailAddressEntity

    override fun findById(
        id: Int,
    ): EmailAddressEntity? =
        queryFactory
            .selectFrom(emailAddress)
            .where(emailAddress.id.eq(id))
            .fetchOne()
}