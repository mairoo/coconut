package kr.co.pincoin.api.infra.user.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProfileQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProfileQueryRepository {
}