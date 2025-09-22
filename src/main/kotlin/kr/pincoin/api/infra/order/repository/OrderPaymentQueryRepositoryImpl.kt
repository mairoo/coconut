package kr.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderPaymentEntity
import kr.pincoin.api.infra.order.entity.QOrderEntity
import kr.pincoin.api.infra.order.entity.QOrderPaymentEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import kr.pincoin.api.infra.user.entity.QUserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class OrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderPaymentQueryRepository {
    private val orderPayment = QOrderPaymentEntity.orderPaymentEntity
    private val order = QOrderEntity.orderEntity
    private val user = QUserEntity.userEntity

    override fun findById(
        id: Long,
    ): OrderPaymentEntity? =
        queryFactory
            .selectFrom(orderPayment)
            .where(orderPayment.id.eq(id))
            .fetchOne()

    override fun findOrderPayment(
        paymentId: Long,
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity? =
        queryFactory
            .selectFrom(orderPayment)
            .leftJoin(order).on(orderPayment.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(
                eqId(paymentId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findOrderPayment(
        criteria: OrderPaymentSearchCriteria,
    ): OrderPaymentEntity? =
        queryFactory
            .selectFrom(orderPayment)
            .leftJoin(order).on(orderPayment.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderPayments(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
    ): Page<OrderPaymentEntity> = executePageQuery(
        criteria,
        pageable,
    ) { baseQuery -> baseQuery.select(orderPayment) }

    private fun <T> executePageQuery(
        criteria: OrderPaymentSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderPayment)
            .leftJoin(order).on(orderPayment.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                orderPayment.dateTimeFields.created.desc(),
                orderPayment.id.desc()
            )
            .fetch()

        val countQuery = {
            createBaseQuery()
                .select(orderPayment.count())
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery,
        )
    }

    private fun getCommonWhereConditions(
        criteria: OrderPaymentSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderId(criteria.orderId),
        eqAccount(criteria.account),
        eqAmount(criteria.amount),
        eqBalance(criteria.balance),
        eqReceived(criteria.received),
        goeStartDateTime(criteria.startDateTime),
        loeEndDateTime(criteria.endDateTime),
        eqIsActive(criteria.isActive),
        eqIsRemoved(criteria.isRemoved),
        eqOrderNumber(criteria.orderNumber),
        eqOrderStatus(criteria.orderStatus),
        eqOrderIsActive(criteria.orderIsActive),
        eqOrderIsRemoved(criteria.orderIsRemoved),
        eqUserId(criteria.userId),
        eqUserEmail(criteria.userEmail),
        eqUserIsActive(criteria.userIsActive),
    )

    private fun eqId(
        paymentId: Long?,
    ): BooleanExpression? =
        paymentId?.let { orderPayment.id.eq(it) }

    private fun eqOrderId(
        orderId: Long?,
    ): BooleanExpression? =
        orderId?.let { orderPayment.orderId.eq(it) }

    private fun eqAccount(
        account: Int?,
    ): BooleanExpression? =
        account?.let { orderPayment.account.eq(it) }

    private fun eqAmount(
        amount: java.math.BigDecimal?,
    ): BooleanExpression? =
        amount?.let { orderPayment.amount.eq(it) }

    private fun eqBalance(
        balance: java.math.BigDecimal?,
    ): BooleanExpression? =
        balance?.let { orderPayment.balance.eq(it) }

    private fun eqReceived(
        received: java.time.LocalDateTime?,
    ): BooleanExpression? =
        received?.let { orderPayment.received.eq(it) }

    private fun goeStartDateTime(
        startDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        startDateTime?.let { orderPayment.dateTimeFields.created.goe(it) }

    private fun loeEndDateTime(
        endDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        endDateTime?.let { orderPayment.dateTimeFields.created.loe(it) }

    private fun eqIsActive(
        isActive: Boolean?,
    ): BooleanExpression? =
        isActive?.let { orderPayment.removalFields.isRemoved.eq(!it) }

    private fun eqIsRemoved(
        isRemoved: Boolean?,
    ): BooleanExpression? =
        isRemoved?.let { orderPayment.removalFields.isRemoved.eq(it) }

    private fun eqOrderNumber(
        orderNumber: String?,
    ): BooleanExpression? =
        orderNumber?.let {
            try {
                val uuid = UUID.fromString(it)
                order.orderNo.eq(uuid)
            } catch (_: IllegalArgumentException) {
                null
            }
        }

    private fun eqOrderStatus(
        orderStatus: String?,
    ): BooleanExpression? =
        orderStatus?.let { order.status.stringValue().eq(it) }

    private fun eqOrderIsActive(
        orderIsActive: Boolean?,
    ): BooleanExpression? =
        orderIsActive?.let { order.removalFields.isRemoved.eq(!it) }

    private fun eqOrderIsRemoved(
        orderIsRemoved: Boolean?,
    ): BooleanExpression? =
        orderIsRemoved?.let { order.removalFields.isRemoved.eq(it) }

    private fun eqUserId(
        userId: Int?,
    ): BooleanExpression? =
        userId?.let { order.userId.eq(it) }

    private fun eqUserEmail(
        userEmail: String?,
    ): BooleanExpression? =
        userEmail?.let { user.email.eq(it) }

    private fun eqUserIsActive(
        userIsActive: Boolean?,
    ): BooleanExpression? =
        userIsActive?.let { user.isActive.eq(it) }
}