package kr.co.pincoin.api.infra.catalog.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.infra.catalog.entity.ProductEntity
import kr.co.pincoin.api.infra.catalog.entity.QCategoryEntity
import kr.co.pincoin.api.infra.catalog.entity.QProductEntity
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductQueryRepository {
    private val product = QProductEntity.productEntity
    private val category = QCategoryEntity.categoryEntity

    override fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(
                eqProductId(id),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findProduct(
        code: String,
        criteria: ProductSearchCriteria,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(*getCommonWhereConditions(criteria.copy(code = code)))
            .fetchOne()

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductEntity> =
        queryFactory
            .selectFrom(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(*getCommonWhereConditions(criteria))
            .orderBy(product.position.asc(), product.id.desc())
            .fetch()

    override fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductEntity> = executePageQuery(
        criteria,
        pageable
    ) { baseQuery ->
        baseQuery.select(product) // lazy select projection
    }

    private fun <T> executePageQuery(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(product.id.desc())
            .fetch()

        val countQuery = {
            createBaseQuery()
                .select(product.count())
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
    }

    private fun getCommonWhereConditions(
        criteria: ProductSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        // Product 조건
        eqProductName(criteria.name),
        eqProductSubtitle(criteria.subtitle),
        eqProductCode(criteria.code),
        inProductCodes(criteria.codes),
        eqProductCategoryId(criteria.categoryId),
        eqProductPg(criteria.pg),
        eqProductStatus(criteria.status),
        isProductRemoved(criteria.isRemoved),
        eqProductStock(criteria.stock),

        // Category 조건
        eqCategoryTitle(criteria.categoryTitle),
        eqCategorySlug(criteria.categorySlug),
        eqCategoryPg(criteria.categoryPg)
    )

    private fun eqProductId(id: Long): BooleanExpression =
        product.id.eq(id)

    private fun eqProductName(name: String?): BooleanExpression? =
        name?.let { product.name.eq(it) }

    private fun eqProductSubtitle(subtitle: String?): BooleanExpression? =
        subtitle?.let { product.subtitle.eq(it) }

    private fun eqProductCode(code: String?): BooleanExpression? =
        code?.let { product.code.eq(it) }

    private fun inProductCodes(codes: Collection<String>?): BooleanExpression? =
        if (!codes.isNullOrEmpty()) product.code.`in`(codes) else null

    private fun eqProductCategoryId(categoryId: Long?): BooleanExpression? =
        categoryId?.let { product.categoryId.eq(it) }

    private fun eqProductPg(pg: Boolean?): BooleanExpression? =
        pg?.let { product.pg.eq(it) }

    private fun eqProductStatus(status: ProductStatus?): BooleanExpression? =
        status?.let { product.status.eq(it) }

    private fun isProductRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { product.removalFields.isRemoved.eq(it) }

    private fun eqProductStock(stock: ProductStock?): BooleanExpression? =
        stock?.let { product.stock.eq(it) }

    private fun eqCategoryTitle(title: String?): BooleanExpression? =
        title?.let { category.title.eq(it) }

    private fun eqCategorySlug(slug: String?): BooleanExpression? =
        slug?.let { category.slug.eq(it) }

    private fun eqCategoryPg(pg: Boolean?): BooleanExpression? =
        pg?.let { category.pg.eq(it) }
}