package kr.pincoin.api.infra.inventory.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.domain.inventory.enums.ProductStatus
import kr.pincoin.api.infra.inventory.entity.ProductEntity
import kr.pincoin.api.infra.inventory.entity.QProductEntity
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
import org.springframework.stereotype.Repository

@Repository
class ProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : ProductQueryRepository {
    private val product = QProductEntity.productEntity

    override fun findById(
        id: Long,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .where(product.id.eq(id))
            .fetchOne()

    override fun findProduct(
        productId: Long,
        criteria: ProductSearchCriteria,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .where(
                eqId(productId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findProduct(
        criteria: ProductSearchCriteria,
    ): ProductEntity? =
        queryFactory
            .selectFrom(product)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findProducts(
        criteria: ProductSearchCriteria,
    ): List<ProductEntity> =
        queryFactory
            .selectFrom(product)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(
                product.position.asc(),
                product.dateTimeFields.created.desc(),
                product.name.asc()
            )
            .fetch()

    private fun getCommonWhereConditions(
        criteria: ProductSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        likeName(criteria.name),
        likeSubtitle(criteria.subtitle),
        eqCode(criteria.code),
        inCodes(criteria.codes),
        eqCategoryId(criteria.categoryId),
        eqStoreId(criteria.storeId),
        eqStatus(criteria.status),
        eqPg(criteria.pg),
        eqIsRemoved(criteria.isRemoved),
    )

    private fun eqId(productId: Long?): BooleanExpression? =
        productId?.let { product.id.eq(it) }

    private fun likeName(name: String?): BooleanExpression? =
        name?.let { product.name.contains(it) }

    private fun likeSubtitle(subtitle: String?): BooleanExpression? =
        subtitle?.let { product.subtitle.contains(it) }

    private fun eqCode(code: String?): BooleanExpression? =
        code?.let { product.code.eq(it) }

    private fun inCodes(codes: List<String>?): BooleanExpression? =
        codes?.takeIf { it.isNotEmpty() }?.let { product.code.`in`(it) }

    private fun eqCategoryId(categoryId: Long?): BooleanExpression? =
        categoryId?.let { product.categoryId.eq(it) }

    private fun eqStoreId(storeId: Long?): BooleanExpression? =
        storeId?.let { product.storeId.eq(it) }

    private fun eqStatus(status: ProductStatus?): BooleanExpression? =
        status?.let { product.status.eq(it) }

    private fun eqPg(pg: Boolean?): BooleanExpression? =
        pg?.let { product.pg.eq(it) }

    private fun eqIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { product.removalFields.isRemoved.eq(it) }
}