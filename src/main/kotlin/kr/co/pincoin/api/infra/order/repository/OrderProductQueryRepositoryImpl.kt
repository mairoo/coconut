package kr.co.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.order.entity.OrderProductEntity
import kr.co.pincoin.api.infra.order.entity.QOrderProductEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class OrderProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductQueryRepository {
    private val orderProduct = QOrderProductEntity.orderProductEntity

    override fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity? =
        queryFactory
            .selectFrom(orderProduct)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
    ): List<OrderProductEntity> =
        queryFactory
            .selectFrom(orderProduct)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(orderProduct.id.desc())
            .fetch()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(orderProduct) }

    private fun <T> executePageQuery(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderProduct)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderProduct.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(orderProduct.count())
                .from(orderProduct)
                .where(*whereConditions)
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
    }

    private fun getCommonWhereConditions(
        criteria: OrderProductSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderProductId(criteria.id),
        eqOrderProductOrderId(criteria.orderId),
        eqOrderProductCode(criteria.code),
        eqOrderProductName(criteria.name),
        eqOrderProductSubtitle(criteria.subtitle),
        eqOrderProductIsRemoved(criteria.isRemoved)
    )

    private fun eqOrderProductId(id: Long?): BooleanExpression? =
        id?.let { orderProduct.id.eq(it) }

    private fun eqOrderProductOrderId(orderId: Long?): BooleanExpression? =
        orderId?.let { orderProduct.orderId.eq(it) }

    private fun eqOrderProductCode(code: String?): BooleanExpression? =
        code?.let { orderProduct.code.eq(it) }

    private fun eqOrderProductName(name: String?): BooleanExpression? =
        name?.let { orderProduct.name.eq(it) }

    private fun eqOrderProductSubtitle(subtitle: String?): BooleanExpression? =
        subtitle?.let { orderProduct.subtitle.eq(it) }

    private fun eqOrderProductIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { orderProduct.removalFields.isRemoved.eq(it) }
}