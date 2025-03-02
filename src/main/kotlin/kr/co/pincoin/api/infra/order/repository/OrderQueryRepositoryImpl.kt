package kr.co.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.infra.order.entity.OrderEntity
import kr.co.pincoin.api.infra.order.entity.QOrderEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime
import java.util.*

@Repository
class OrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderQueryRepository {
    private val order = QOrderEntity.orderEntity

    override fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<OrderEntity> =
        queryFactory
            .selectFrom(order)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(order.dateTimeFields.created.desc(), order.id.desc())
            .fetch()

    override fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(order) }

    private fun <T> executePageQuery(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(order)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(order.dateTimeFields.created.desc(), order.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(order.count())
                .from(order)
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
        criteria: OrderSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderId(criteria.id),
        eqOrderNo(criteria.orderNo),
        eqOrderUserId(criteria.userId),
        eqOrderFullname(criteria.fullname),
        eqOrderIpAddress(criteria.ipAddress),
        eqOrderPaymentMethod(criteria.paymentMethod),
        eqOrderTransactionId(criteria.transactionId),
        eqOrderStatus(criteria.status),
        eqOrderVisible(criteria.visible),
        eqOrderCurrency(criteria.currency),
        eqOrderParentId(criteria.parentId),
        eqOrderSuspicious(criteria.suspicious),
        eqOrderIsRemoved(criteria.isRemoved),
        betweenOrderCreated(criteria.createdFrom, criteria.createdTo)
    )

    private fun eqOrderId(id: Long?): BooleanExpression? =
        id?.let { order.id.eq(it) }

    private fun eqOrderNo(orderNo: UUID?): BooleanExpression? =
        orderNo?.let { order.orderNo.eq(it) }

    private fun eqOrderUserId(userId: Long?): BooleanExpression? =
        userId?.let { order.userId.eq(it) }

    private fun eqOrderFullname(fullname: String?): BooleanExpression? =
        fullname?.let { order.fullname.eq(it) }

    private fun eqOrderIpAddress(ipAddress: String?): BooleanExpression? =
        ipAddress?.let { order.ipAddress.eq(it) }

    private fun eqOrderPaymentMethod(paymentMethod: OrderPaymentMethod?): BooleanExpression? =
        paymentMethod?.let { order.paymentMethod.eq(it) }

    private fun eqOrderTransactionId(transactionId: String?): BooleanExpression? =
        transactionId?.let { order.transactionId.eq(it) }

    private fun eqOrderStatus(status: OrderStatus?): BooleanExpression? =
        status?.let { order.status.eq(it) }

    private fun eqOrderVisible(visible: OrderVisibility?): BooleanExpression? =
        visible?.let { order.visible.eq(it) }

    private fun eqOrderCurrency(currency: OrderCurrency?): BooleanExpression? =
        currency?.let { order.currency.eq(it) }

    private fun eqOrderParentId(parentId: Long?): BooleanExpression? =
        parentId?.let { order.parentId.eq(it) }

    private fun eqOrderSuspicious(suspicious: Boolean?): BooleanExpression? =
        suspicious?.let { order.suspicious.eq(it) }

    private fun eqOrderIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { order.removalFields.isRemoved.eq(it) }

    private fun betweenOrderCreated(
        createdFrom: ZonedDateTime?,
        createdTo: ZonedDateTime?
    ): BooleanExpression? {
        return when {
            createdFrom != null && createdTo != null -> order.dateTimeFields.created.between(
                createdFrom,
                createdTo
            )

            createdFrom != null -> order.dateTimeFields.created.goe(createdFrom)
            createdTo != null -> order.dateTimeFields.created.loe(createdTo)
            else -> null
        }
    }
}