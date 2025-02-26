package kr.co.pincoin.api.infra.catalog.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class CategoryQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryQueryRepository {
}