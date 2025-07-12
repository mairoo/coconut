package kr.pincoin.api.infra.social.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class EmailAddressQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : EmailAddressQueryRepository {
}