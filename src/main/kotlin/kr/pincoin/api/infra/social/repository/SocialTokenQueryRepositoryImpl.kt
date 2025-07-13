package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.QSocialTokenEntity
import kr.pincoin.api.infra.social.entity.SocialTokenEntity
import org.springframework.stereotype.Repository

@Repository
class SocialTokenQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialTokenQueryRepository {
    private val socialToken = QSocialTokenEntity.socialTokenEntity

    override fun findById(
        id: Int,
    ): SocialTokenEntity? =
        queryFactory
            .selectFrom(socialToken)
            .where(socialToken.id.eq(id))
            .fetchOne()
}