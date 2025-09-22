package kr.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import kr.pincoin.api.infra.order.entity.QOrderEntity
import kr.pincoin.api.infra.order.entity.QOrderProductEntity
import kr.pincoin.api.infra.order.entity.QOrderProductVoucherEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.pincoin.api.infra.user.entity.QUserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class OrderProductVoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductVoucherQueryRepository {
    private val orderProductVoucher = QOrderProductVoucherEntity.orderProductVoucherEntity
    private val orderProduct = QOrderProductEntity.orderProductEntity
    private val order = QOrderEntity.orderEntity
    private val user = QUserEntity.userEntity

    override fun findById(
        id: Long,
    ): OrderProductVoucherEntity? =
        queryFactory
            .selectFrom(orderProductVoucher)
            .where(orderProductVoucher.id.eq(id))
            .fetchOne()

    override fun findOrderProductVoucher(
        voucherId: Long,
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity? =
        queryFactory
            .selectFrom(orderProductVoucher)
            .innerJoin(orderProduct).on(orderProductVoucher.orderProductId.eq(orderProduct.id))
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(
                eqId(voucherId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucherEntity? =
        queryFactory
            .selectFrom(orderProductVoucher)
            .innerJoin(orderProduct).on(orderProductVoucher.orderProductId.eq(orderProduct.id))
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderProductVouchers(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucherEntity> = executePageQuery(
        criteria,
        pageable,
    ) { baseQuery -> baseQuery.select(orderProductVoucher) }

    private fun <T> executePageQuery(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderProductVoucher)
            .innerJoin(orderProduct).on(orderProductVoucher.orderProductId.eq(orderProduct.id))
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                orderProductVoucher.dateTimeFields.created.desc(),
                orderProductVoucher.id.desc()
            )
            .fetch()

        val countQuery = {
            createBaseQuery()
                .select(orderProductVoucher.count())
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery,
        )
    }

    private fun getCommonWhereConditions(
        criteria: OrderProductVoucherSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderProductId(criteria.orderProductId),
        eqCode(criteria.code),
        eqRemarks(criteria.remarks),
        eqRevoked(criteria.revoked),
        goeStartDateTime(criteria.startDateTime),
        loeEndDateTime(criteria.endDateTime),
        eqIsActive(criteria.isActive),
        eqIsRemoved(criteria.isRemoved),
        eqOrderId(criteria.orderId),
        eqQuantity(criteria.quantity),
        eqOrderNumber(criteria.orderNumber),
        eqOrderStatus(criteria.orderStatus),
        eqOrderIsActive(criteria.orderIsActive),
        eqOrderIsRemoved(criteria.orderIsRemoved),
        eqUserId(criteria.userId),
        eqUserEmail(criteria.userEmail),
        eqUserIsActive(criteria.userIsActive),
    )

    private fun eqId(
        voucherId: Long?,
    ): BooleanExpression? =
        voucherId?.let { orderProductVoucher.id.eq(it) }

    private fun eqOrderProductId(
        orderProductId: Long?,
    ): BooleanExpression? =
        orderProductId?.let { orderProductVoucher.orderProductId.eq(it) }

    private fun eqCode(
        code: String?,
    ): BooleanExpression? =
        code?.let { orderProductVoucher.code.eq(it) }

    private fun eqRemarks(
        remarks: String?,
    ): BooleanExpression? =
        remarks?.let { orderProductVoucher.remarks.containsIgnoreCase(it) }

    private fun eqRevoked(
        revoked: Boolean?,
    ): BooleanExpression? =
        revoked?.let { orderProductVoucher.revoked.eq(it) }

    private fun goeStartDateTime(
        startDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        startDateTime?.let { orderProductVoucher.dateTimeFields.created.goe(it) }

    private fun loeEndDateTime(
        endDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        endDateTime?.let { orderProductVoucher.dateTimeFields.created.loe(it) }

    private fun eqIsActive(
        isActive: Boolean?,
    ): BooleanExpression? =
        isActive?.let { orderProductVoucher.removalFields.isRemoved.eq(!it) }

    private fun eqIsRemoved(
        isRemoved: Boolean?,
    ): BooleanExpression? =
        isRemoved?.let { orderProductVoucher.removalFields.isRemoved.eq(it) }

    private fun eqOrderId(
        orderId: Long?,
    ): BooleanExpression? =
        orderId?.let { orderProduct.orderId.eq(it) }

    private fun eqQuantity(
        quantity: Int?,
    ): BooleanExpression? =
        quantity?.let { orderProduct.quantity.eq(it) }

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