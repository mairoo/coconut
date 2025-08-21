package kr.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderEntity
import kr.pincoin.api.infra.order.entity.QOrderEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.pincoin.api.infra.user.entity.QUserEntity
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class OrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderQueryRepository {
    private val order = QOrderEntity.orderEntity
    private val user = QUserEntity.userEntity

    override fun findById(
        id: Long,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .where(order.id.eq(id))
            .fetchOne()

    override fun findOrder(
        orderId: Long,
        criteria: OrderSearchCriteria,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .leftJoin(user).on(order.userId.eq(user.id.intValue()))
            .where(
                eqId(orderId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .leftJoin(user).on(order.userId.eq(user.id.intValue()))
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<OrderEntity> =
        queryFactory
            .selectFrom(order)
            .leftJoin(user).on(order.userId.eq(user.id.intValue()))
            .where(*getCommonWhereConditions(criteria))
            .orderBy(
                order.dateTimeFields.created.desc(),
                order.id.desc()
            )
            .fetch()

    private fun getCommonWhereConditions(
        criteria: OrderSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderNumber(criteria.orderNumber),
        eqStatus(criteria.status),
        eqPaymentMethod(criteria.paymentMethod),
        eqPaymentStatus(criteria.paymentStatus),
        goeStartDateTime(criteria.startDateTime),
        loeEndDateTime(criteria.endDateTime),
        eqIsActive(criteria.isActive),
        eqIsRemoved(criteria.isRemoved),
        eqUserId(criteria.userId),
        eqUserEmail(criteria.userEmail),
        eqUserIsActive(criteria.userIsActive),
    )

    private fun eqId(
        orderId: Long?,
    ): BooleanExpression? =
        orderId?.let { order.id.eq(it) }

    private fun eqOrderNumber(
        orderNumber: String?,
    ): BooleanExpression? =
        orderNumber?.let {
            try {
                val uuid = UUID.fromString(it)
                order.orderNo.eq(uuid)
            } catch (_: IllegalArgumentException) {
                null // 잘못된 UUID 형식인 경우 조건 무시
            }
        }

    private fun eqStatus(
        status: String?,
    ): BooleanExpression? =
        status?.let { order.status.stringValue().eq(it) }

    private fun eqPaymentMethod(
        paymentMethod: String?,
    ): BooleanExpression? =
        paymentMethod?.let { order.paymentMethod.stringValue().eq(it) }

    private fun eqPaymentStatus(
        paymentStatus: String?,
    ): BooleanExpression? =
        paymentStatus?.let { order.status.stringValue().eq(it) } // status와 동일하게 처리

    private fun goeStartDateTime(
        startDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        startDateTime?.let { order.dateTimeFields.created.goe(it) }

    private fun loeEndDateTime(
        endDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        endDateTime?.let { order.dateTimeFields.created.loe(it) }

    private fun eqIsActive(
        isActive: Boolean?,
    ): BooleanExpression? =
        isActive?.let { order.removalFields.isRemoved.eq(!it) } // isActive는 isRemoved의 반대

    private fun eqIsRemoved(
        isRemoved: Boolean?,
    ): BooleanExpression? =
        isRemoved?.let { order.removalFields.isRemoved.eq(it) }

    private fun eqUserId(
        userId: Long?,
    ): BooleanExpression? =
        userId?.let { order.userId.eq(it.toInt()) }

    private fun eqUserEmail(
        userEmail: String?,
    ): BooleanExpression? =
        userEmail?.let { user.email.eq(it) }

    private fun eqUserIsActive(
        userIsActive: Boolean?,
    ): BooleanExpression? =
        userIsActive?.let { user.isActive.eq(it) }
}