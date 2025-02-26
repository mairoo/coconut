package kr.co.pincoin.api.infra.catalog.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.catalog.entity.CategoryEntity
import kr.co.pincoin.api.infra.catalog.entity.QCategoryEntity
import kr.co.pincoin.api.infra.catalog.repository.criteria.CategorySearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class CategoryQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : CategoryQueryRepository {
    private val category = QCategoryEntity.categoryEntity

    override fun findCategory(
        id: Long,
        criteria: CategorySearchCriteria,
    ): CategoryEntity? =
        queryFactory
            .selectFrom(category)
            .where(
                eqCategoryId(id),
                *getCommonWhereConditions(criteria)
            )
            .fetchOne()

    override fun findCategories(
        criteria: CategorySearchCriteria,
    ): List<CategoryEntity> =
        queryFactory
            .selectFrom(category)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(category.id.desc())
            .fetch()

    override fun findCategories(
        criteria: CategorySearchCriteria,
        pageable: Pageable,
    ): Page<CategoryEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(category) }

    private fun <T> executePageQuery(
        criteria: CategorySearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val baseQuery = queryFactory
            .from(category)
            .where(*getCommonWhereConditions(criteria))

        val query = selectClause(baseQuery)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(category.id.desc())

        val results = query.fetch()

        return PageableExecutionUtils.getPage(
            results,
            pageable
        ) {
            baseQuery.select(category.count()).fetchOne() ?: 0L
        }
    }

    private fun getCommonWhereConditions(
        criteria: CategorySearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqCategoryTitle(criteria.title),
        eqCategorySlug(criteria.slug),
        eqCategoryStoreId(criteria.storeId),
        containsCategoryDescription(criteria.description)
    )

    private fun eqCategoryId(id: Long): BooleanExpression =
        category.id.eq(id)

    private fun eqCategoryTitle(title: String?): BooleanExpression? =
        title?.let { category.title.eq(it) }

    private fun eqCategorySlug(slug: String?): BooleanExpression? =
        slug?.let { category.slug.eq(it) }

    private fun eqCategoryStoreId(storeId: Long?): BooleanExpression? =
        storeId?.let { category.storeId.eq(it) }

    private fun containsCategoryDescription(description: String?): BooleanExpression? =
        description?.let { category.description.contains(it) }
}