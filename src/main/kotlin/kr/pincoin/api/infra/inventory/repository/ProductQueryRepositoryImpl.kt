package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import org.springframework.stereotype.Repository

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
): ProductQueryRepository {
}