package kr.co.pincoin.api.infra.catalog.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductQueryRepository {
}