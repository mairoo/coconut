package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class SocialAppSitesQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : SocialAppSitesQueryRepository {
}