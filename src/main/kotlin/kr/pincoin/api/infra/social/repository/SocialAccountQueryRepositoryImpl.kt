package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.QSocialAccountEntity
import kr.pincoin.api.infra.social.entity.SocialAccountEntity
import org.springframework.stereotype.Repository

@Repository
class SocialAccountQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialAccountQueryRepository {
    private val socialAccount = QSocialAccountEntity.socialAccountEntity

    override fun findById(
        id: Int,
    ): SocialAccountEntity? =
        queryFactory
            .selectFrom(socialAccount)
            .where(socialAccount.id.eq(id))
            .fetchOne()
}