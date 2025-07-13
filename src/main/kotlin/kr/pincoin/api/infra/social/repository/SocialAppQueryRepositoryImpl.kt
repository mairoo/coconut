package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.QSocialAppEntity
import kr.pincoin.api.infra.social.entity.SocialAppEntity
import org.springframework.stereotype.Repository

@Repository
class SocialAppQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialAppQueryRepository {
    private val socialApp = QSocialAppEntity.socialAppEntity

    override fun findById(
        id: Int,
    ): SocialAppEntity? =
        queryFactory
            .selectFrom(socialApp)
            .where(socialApp.id.eq(id))
            .fetchOne()
}