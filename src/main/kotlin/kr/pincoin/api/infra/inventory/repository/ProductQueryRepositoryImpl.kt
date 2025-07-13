package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.ProductEntity
import kr.pincoin.api.infra.inventory.entity.QProductEntity
import org.springframework.stereotype.Repository

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
): ProductQueryRepository {
    private val product = QProductEntity.productEntity

    override fun findById(
        id: Long,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .where(product.id.eq(id))
            .fetchOne()
}