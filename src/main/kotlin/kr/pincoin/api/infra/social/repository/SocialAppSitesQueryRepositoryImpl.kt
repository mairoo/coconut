package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.social.entity.QSocialAppSitesEntity
import kr.pincoin.api.infra.social.entity.SocialAppSitesEntity
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialAppSitesQueryRepository {
    private val socialAppSites = QSocialAppSitesEntity.socialAppSitesEntity

    override fun findById(
        id: Int,
    ): SocialAppSitesEntity? =
        queryFactory
            .selectFrom(socialAppSites)
            .where(socialAppSites.id.eq(id))
            .fetchOne()
}