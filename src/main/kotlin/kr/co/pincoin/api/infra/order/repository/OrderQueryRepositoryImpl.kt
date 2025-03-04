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
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import kr.co.pincoin.api.infra.order.repository.projection.QOrderUserProfileProjection
import kr.co.pincoin.api.infra.user.entity.QProfileEntity
import kr.co.pincoin.api.infra.user.entity.QUserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.net.InetAddress
import java.time.ZonedDateTime
import java.util.*

@Repository
class OrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderQueryRepository {
    private val order = QOrderEntity.orderEntity
    private val user = QUserEntity.userEntity
    private val profile = QProfileEntity.profileEntity

    override fun findOrder(
        criteria: OrderSearchCriteria,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderWithUserProfile(
        criteria: OrderSearchCriteria,
    ): OrderUserProfileProjection? =
        queryFactory
            .select(createProjection())
            .from(order)
            .innerJoin(user).on(order.userId.eq(user.id))
            .innerJoin(profile).on(user.id.eq(profile.userId))
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

    override fun findOrdersWithUserProfile(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection> {
        val whereConditions = getCommonWhereConditions(criteria)

        val query = queryFactory
            .select(createProjection())
            .from(order)
            .innerJoin(user).on(order.userId.eq(user.id))
            .innerJoin(profile).on(user.id.eq(profile.userId))
            .where(*whereConditions)
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(order.dateTimeFields.created.desc(), order.id.desc())

        val results = query.fetch()

        val countQuery = {
            queryFactory
                .select(order.count())
                .from(order)
                // .innerJoin(user).on(order.userId.eq(user.id))
                // .innerJoin(profile).on(user.id.eq(profile.userId))
                .where(*whereConditions)
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results, pageable, countQuery
        )
    }

    private fun createProjection() = QOrderUserProfileProjection(
        // Order 정보
        order.id,
        order.dateTimeFields.created,
        order.dateTimeFields.modified,
        order.removalFields.isRemoved,
        order.orderNo,
        order.ipAddress,
        order.fullname,
        order.userAgent,
        order.acceptLanguage,
        order.paymentMethod,
        order.transactionId,
        order.status,
        order.visible,
        order.totalListPrice,
        order.totalSellingPrice,
        order.currency,
        order.message,
        order.parentId,
        order.suspicious,

        // User 정보
        user.id,
        user.username,
        user.dateJoined,
        user.lastLogin,
        user.isSuperuser,
        user.firstName,
        user.lastName,
        user.email,
        user.isStaff,
        user.isActive,

        // Profile 정보
        profile.id,
        profile.dateTimeFields.created,
        profile.dateTimeFields.modified,
        profile.phone,
        profile.address,
        profile.phoneVerified,
        profile.documentVerified,
        profile.photoId,
        profile.card,
        profile.totalOrderCount,
        profile.lastPurchased,
        profile.maxPrice,
        profile.averagePrice,
        profile.memo,
        profile.phoneVerifiedStatus,
        profile.dateOfBirth,
        profile.firstPurchased,
        profile.totalListPrice,
        profile.totalSellingPrice,
        profile.domestic,
        profile.gender,
        profile.telecom,
        profile.notPurchasedMonths,
        profile.repurchased,
        profile.mileage,
        profile.allowOrder
    )

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

    private fun eqOrderUserId(userId: Int?): BooleanExpression? =
        userId?.let { order.userId.eq(it) }

    private fun eqOrderFullname(fullname: String?): BooleanExpression? =
        fullname?.let { order.fullname.eq(it) }

    private fun eqOrderIpAddress(ipAddress: InetAddress?): BooleanExpression? =
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