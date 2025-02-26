package kr.co.pincoin.api.infra.catalog.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.catalog.entity.QCategoryEntity
import kr.co.pincoin.api.infra.catalog.entity.QProductEntity
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.catalog.repository.projection.ProductProjection
import kr.co.pincoin.api.infra.catalog.repository.projection.QProductProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.math.BigDecimal

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductQueryRepository {
    private val product = QProductEntity.productEntity
    private val category = QCategoryEntity.categoryEntity

    override fun findProduct(
        id: Long,
        criteria: ProductSearchCriteria,
    ): ProductProjection? =
        queryFactory
            .select(createProductProjection())
            .from(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(
                eqProductId(id),
                *getCommonWhereConditions(criteria)
            )
            .fetchOne()

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductProjection> =
        queryFactory
            .select(createProductProjection())
            .from(product)
            .join(category).on(product.categoryId.eq(category.id))
            .where(*getCommonWhereConditions(criteria))
            .orderBy(product.position.asc(), product.id.desc())
            .fetch()

    override fun findProducts(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
    ): Page<ProductProjection> = executePageQuery(
        criteria,
        pageable,
    ) { baseQuery ->
        baseQuery
            .join(category).on(product.categoryId.eq(category.id))
            .select(createProductProjection())
    }

    private fun createProductProjection() =
        QProductProjection(
            product.id,
            product.dateTimeFields.created,
            product.dateTimeFields.modified,
            product.removalFields.isRemoved,
            product.name,
            product.subtitle,
            product.code,
            product.listPrice,
            product.sellingPrice,
            product.pg,
            product.pgSellingPrice,
            product.description,
            product.storeId,
            product.categoryId,
            product.position,
            product.status,
            product.stockQuantity,
            product.stock,

            category.title,
            category.slug,
            category.thumbnail,
            category.description,
            category.discountRate
        )

    private fun <T> executePageQuery(
        criteria: ProductSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val baseQuery = queryFactory
            .from(product)
            .where(*getCommonWhereConditions(criteria))

        val query = selectClause(baseQuery)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(product.position.asc(), product.id.desc())

        val results = query.fetch()

        return PageableExecutionUtils.getPage(
            results,
            pageable
        ) {
            baseQuery.select(product.count()).fetchOne() ?: 0L
        }
    }

    private fun getCommonWhereConditions(
        criteria: ProductSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        // Product 조건
        eqProductName(criteria.name),
        eqProductCode(criteria.code),
        eqProductStoreId(criteria.storeId),
        eqProductCategoryId(criteria.categoryId),
        goeProductSellingPrice(criteria.minSellingPrice),
        loeProductSellingPrice(criteria.maxSellingPrice),
        eqProductPg(criteria.pg),
        eqProductStatus(criteria.status),
        isProductRemoved(criteria.isRemoved),
        isProductInStock(criteria.inStock),

        // Category 조건
        eqCategoryTitle(criteria.categoryTitle),
        eqCategorySlug(criteria.categorySlug),
        eqCategoryPg(criteria.categoryPg)
    )

    private fun eqProductId(id: Long): BooleanExpression =
        product.id.eq(id)

    private fun eqProductName(name: String?): BooleanExpression? =
        name?.let { product.name.eq(it) }

    private fun eqProductCode(code: String?): BooleanExpression? =
        code?.let { product.code.eq(it) }

    private fun eqProductStoreId(storeId: Long?): BooleanExpression? =
        storeId?.let { product.storeId.eq(it) }

    private fun eqProductCategoryId(categoryId: Long?): BooleanExpression? =
        categoryId?.let { product.categoryId.eq(it) }

    private fun goeProductSellingPrice(minPrice: BigDecimal?): BooleanExpression? =
        minPrice?.let { product.sellingPrice.goe(it) }

    private fun loeProductSellingPrice(maxPrice: BigDecimal?): BooleanExpression? =
        maxPrice?.let { product.sellingPrice.loe(it) }

    private fun eqProductPg(pg: Boolean?): BooleanExpression? =
        pg?.let { product.pg.eq(it) }

    private fun eqProductStatus(status: Int?): BooleanExpression? =
        status?.let { product.status.eq(it) }

    private fun isProductRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { product.removalFields.isRemoved.eq(it) }

    private fun isProductInStock(inStock: Boolean?): BooleanExpression? =
        inStock?.let {
            if (it) {
                product.stock.gt(0)
            } else {
                product.stock.eq(0)
            }
        }

    private fun eqCategoryTitle(title: String?): BooleanExpression? =
        title?.let { category.title.eq(it) }

    private fun eqCategorySlug(slug: String?): BooleanExpression? =
        slug?.let { category.slug.eq(it) }

    private fun eqCategoryPg(pg: Boolean?): BooleanExpression? =
        pg?.let { category.pg.eq(it) }
}