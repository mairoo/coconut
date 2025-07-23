package kr.pincoin.api.infra.inventory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.CategoryEntity
import kr.pincoin.api.infra.inventory.entity.QCategoryEntity
import kr.pincoin.api.infra.inventory.repository.criteria.CategorySearchCriteria
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

    override fun findCategory(
        categoryId: Long,
        criteria: CategorySearchCriteria,
    ): CategoryEntity? =
        queryFactory
            .selectFrom(category)
            .where(
                eqId(categoryId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findCategory(
        criteria: CategorySearchCriteria,
    ): CategoryEntity? =
        queryFactory
            .selectFrom(category)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findCategories(
        criteria: CategorySearchCriteria
    ): List<CategoryEntity> =
        queryFactory
            .selectFrom(category)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(
                category.level.asc(),
                category.lft.asc(),
                category.title.asc()
            )
            .fetch()

    private fun getCommonWhereConditions(
        criteria: CategorySearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqTitle(criteria.title),
        eqSlug(criteria.slug),
        eqStoreId(criteria.storeId),
        eqParentId(criteria.parentId),
        eqLevel(criteria.level),
        eqTreeId(criteria.treeId),
        eqPg(criteria.pg),
    )

    private fun eqId(
        categoryId: Long?,
    ): BooleanExpression? =
        categoryId?.let { category.id.eq(it) }

    private fun eqTitle(
        title: String?,
    ): BooleanExpression? =
        title?.let { category.title.contains(it) }

    private fun eqSlug(
        slug: String?,
    ): BooleanExpression? =
        slug?.let { category.slug.eq(it) }

    private fun eqStoreId(
        storeId: Long?,
    ): BooleanExpression? =
        storeId?.let { category.storeId.eq(it) }

    private fun eqParentId(
        parentId: Long?,
    ): BooleanExpression? =
        parentId?.let { category.parentId.eq(it) }

    private fun eqLevel(
        level: Int?,
    ): BooleanExpression? =
        level?.let { category.level.eq(it) }

    private fun eqTreeId(
        treeId: Int?,
    ): BooleanExpression? =
        treeId?.let { category.treeId.eq(it) }

    private fun eqPg(
        pg: Boolean?,
    ): BooleanExpression? =
        pg?.let { category.pg.eq(it) }
}