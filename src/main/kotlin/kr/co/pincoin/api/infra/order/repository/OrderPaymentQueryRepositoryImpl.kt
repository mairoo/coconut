package kr.co.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount
import kr.co.pincoin.api.infra.order.entity.OrderPaymentEntity
import kr.co.pincoin.api.infra.order.entity.QOrderPaymentEntity
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.time.ZonedDateTime

@Repository
class OrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderPaymentQueryRepository {
    private val orderPayment = QOrderPaymentEntity.orderPaymentEntity

    override fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity? =
        queryFactory
            .selectFrom(orderPayment)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
    ): List<OrderPaymentEntity> =
        queryFactory
            .selectFrom(orderPayment)
            .where(*getCommonWhereConditions(criteria))
            .orderBy(orderPayment.dateTimeFields.created.desc(), orderPayment.id.desc())
            .fetch()

    override fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPaymentEntity> =
        executePageQuery(
            criteria,
            pageable = pageable,
        ) { baseQuery -> baseQuery.select(orderPayment) }

    private fun <T> executePageQuery(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderPayment)
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(orderPayment.dateTimeFields.created.desc(), orderPayment.id.desc())
            .fetch()

        val countQuery = {
            queryFactory
                .select(orderPayment.count())
                .from(orderPayment)
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
        criteria: OrderPaymentSearchCriteria,
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderPaymentId(criteria.id),
        eqOrderPaymentOrderId(criteria.orderId),
        eqOrderPaymentAccount(criteria.account),
        eqOrderPaymentIsRemoved(criteria.isRemoved),
        betweenOrderPaymentReceived(criteria.receivedFrom, criteria.receivedTo),
        betweenOrderPaymentCreated(criteria.createdFrom, criteria.createdTo)
    )

    private fun eqOrderPaymentId(id: Long?): BooleanExpression? =
        id?.let { orderPayment.id.eq(it) }

    private fun eqOrderPaymentOrderId(orderId: Long?): BooleanExpression? =
        orderId?.let { orderPayment.orderId.eq(it) }

    private fun eqOrderPaymentAccount(account: PaymentBankAccount?): BooleanExpression? =
        account?.let { orderPayment.account.eq(it) }

    private fun eqOrderPaymentIsRemoved(isRemoved: Boolean?): BooleanExpression? =
        isRemoved?.let { orderPayment.removalFields.isRemoved.eq(it) }

    private fun betweenOrderPaymentReceived(
        receivedFrom: ZonedDateTime?,
        receivedTo: ZonedDateTime?
    ): BooleanExpression? {
        return when {
            receivedFrom != null && receivedTo != null -> orderPayment.received.between(
                receivedFrom,
                receivedTo
            )

            receivedFrom != null -> orderPayment.received.goe(receivedFrom)
            receivedTo != null -> orderPayment.received.loe(receivedTo)
            else -> null
        }
    }

    private fun betweenOrderPaymentCreated(
        createdFrom: ZonedDateTime?,
        createdTo: ZonedDateTime?
    ): BooleanExpression? {
        return when {
            createdFrom != null && createdTo != null -> orderPayment.dateTimeFields.created.between(
                createdFrom,
                createdTo
            )

            createdFrom != null -> orderPayment.dateTimeFields.created.goe(createdFrom)
            createdTo != null -> orderPayment.dateTimeFields.created.loe(createdTo)
            else -> null
        }
    }
}