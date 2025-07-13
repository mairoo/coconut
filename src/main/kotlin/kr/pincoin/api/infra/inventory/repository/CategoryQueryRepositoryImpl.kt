package kr.pincoin.api.infra.inventory.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.CategoryEntity
import kr.pincoin.api.infra.inventory.entity.QCategoryEntity
import org.springframework.stereotype.Repository

@Repository
class CategoryQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryQueryRepository {
    private val category = QCategoryEntity.categoryEntity
    override fun findById(
        id: Long,
    ): CategoryEntity? =
        queryFactory
            .selectFrom(category)
            .where(category.id.eq(id))
            .fetchOne()
}