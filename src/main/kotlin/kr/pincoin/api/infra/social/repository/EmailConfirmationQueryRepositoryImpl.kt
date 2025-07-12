package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class EmailConfirmationQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EmailConfirmationQueryRepository {
}