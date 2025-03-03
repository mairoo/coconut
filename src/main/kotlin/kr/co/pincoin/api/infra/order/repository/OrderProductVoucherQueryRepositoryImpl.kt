package kr.co.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import kr.co.pincoin.api.infra.order.entity.QOrderProductEntity
import kr.co.pincoin.api.infra.order.entity.QOrderProductVoucherEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import kr.co.pincoin.api.infra.order.repository.projection.QOrderProductVoucherProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductVoucherQueryRepository {
    private val orderProductVoucher = QOrderProductVoucherEntity.orderProductVoucherEntity

    override fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity? =
        queryFactory
            .selectFrom(orderProductVoucher)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
    ): List<OrderProductVoucherEntity> =
        queryFactory
            .selectFrom(orderProductVoucher)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(orderProductVoucher.id.desc())
            .fetch()

    override fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(orderProductVoucher) }

    override fun findOrderProductVouchersWithProduct(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherProjection> {
        val orderProduct = QOrderProductEntity.orderProductEntity
        val whereConditions = getCommonWhereConditions(criteria)

        val query = queryFactory
            .select(
                QOrderProductVoucherProjection(
                    // OrderProduct 정보
                    orderProduct.id,
                    orderProduct.dateTimeFields.created,
                    orderProduct.dateTimeFields.modified,
                    orderProduct.removalFields.isRemoved,
                    orderProduct.orderId,
                    orderProduct.code,
                    orderProduct.name,
                    orderProduct.subtitle,
                    orderProduct.listPrice,
                    orderProduct.sellingPrice,
                    orderProduct.quantity,

                    // OrderProductVoucher 정보
                    orderProductVoucher.id,
                    orderProductVoucher.dateTimeFields.created,
                    orderProductVoucher.dateTimeFields.modified,
                    orderProductVoucher.removalFields.isRemoved,
                    orderProductVoucher.orderProductId,
                    orderProductVoucher.code,
                    orderProductVoucher.voucherId,
                    orderProductVoucher.revoked,
                    orderProductVoucher.remarks
                )
            )
            .from(orderProductVoucher)
            .innerJoin(orderProduct).on(orderProductVoucher.orderProductId.eq(orderProduct.id))
            .where(*whereConditions)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderProductVoucher.id.desc())

        val results = query.fetch()

        val countQuery = {
            queryFactory
                .select(orderProductVoucher.count())
                .from(orderProductVoucher)
                .innerJoin(orderProduct).on(orderProductVoucher.orderProductId.eq(orderProduct.id))
                .where(*whereConditions)
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery
        )
    }

    private fun <T> executePageQuery(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderProductVoucher)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderProductVoucher.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(orderProductVoucher.count())
                .from(orderProductVoucher)
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
        criteria: OrderProductVoucherSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderProductVoucherId(criteria.id),
        eqOrderProductVoucherOrderProductId(criteria.orderProductId),
        eqOrderProductVoucherVoucherId(criteria.voucherId),
        eqOrderProductVoucherCode(criteria.code),
        eqOrderProductVoucherRemarks(criteria.remarks),
        eqOrderProductVoucherRevoked(criteria.revoked),
        eqOrderProductVoucherIsRemoved(criteria.isRemoved)
    )

    private fun eqOrderProductVoucherId(id: Long?): BooleanExpression? =
        id?.let { orderProductVoucher.id.eq(it) }

    private fun eqOrderProductVoucherOrderProductId(orderProductId: Long?): BooleanExpression? =
        orderProductId?.let { orderProductVoucher.orderProductId.eq(it) }

    private fun eqOrderProductVoucherVoucherId(voucherId: Long?): BooleanExpression? =
        voucherId?.let { orderProductVoucher.voucherId.eq(it) }

    private fun eqOrderProductVoucherCode(code: String?): BooleanExpression? =
        code?.let { orderProductVoucher.code.eq(it) }

    private fun eqOrderProductVoucherRemarks(remarks: String?): BooleanExpression? =
        remarks?.let { orderProductVoucher.remarks.eq(it) }

    private fun eqOrderProductVoucherRevoked(revoked: Boolean?): BooleanExpression? =
        revoked?.let { orderProductVoucher.revoked.eq(it) }

    private fun eqOrderProductVoucherIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { orderProductVoucher.removalFields.isRemoved.eq(it) }
}