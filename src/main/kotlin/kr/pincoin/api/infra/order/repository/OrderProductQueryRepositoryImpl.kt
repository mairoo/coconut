package kr.pincoin.api.infra.order.repository

import com.querydsl.core.types.dsl.BooleanExpression
import com.querydsl.jpa.impl.JPAQuery
import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.inventory.entity.QProductEntity
import kr.pincoin.api.infra.order.entity.OrderProductEntity
import kr.pincoin.api.infra.order.entity.QOrderEntity
import kr.pincoin.api.infra.order.entity.QOrderProductEntity
import kr.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import kr.pincoin.api.infra.user.entity.QUserEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository
import java.util.*

@Repository
class OrderProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductQueryRepository {
    private val orderProduct = QOrderProductEntity.orderProductEntity
    private val order = QOrderEntity.orderEntity
    private val product = QProductEntity.productEntity
    private val user = QUserEntity.userEntity

    override fun findById(
        id: Long,
    ): OrderProductEntity? =
        queryFactory
            .selectFrom(orderProduct)
            .where(orderProduct.id.eq(id))
            .fetchOne()

    override fun findOrderProduct(
        orderProductId: Long,
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity? =
        queryFactory
            .selectFrom(orderProduct)
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(
                eqId(orderProductId),
                *getCommonWhereConditions(criteria),
            )
            .fetchOne()

    override fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProductEntity? =
        queryFactory
            .selectFrom(orderProduct)
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*getCommonWhereConditions(criteria))
            .fetchOne()

    override fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductEntity> = executePageQuery(
        criteria,
        pageable,
    ) { baseQuery -> baseQuery.select(orderProduct) }

    private fun <T> executePageQuery(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
        selectClause: (JPAQuery<*>) -> JPAQuery<T>
    ): Page<T> {
        val whereConditions = getCommonWhereConditions(criteria)

        fun createBaseQuery() = queryFactory
            .from(orderProduct)
            .innerJoin(order).on(orderProduct.orderId.eq(order.id))
            .leftJoin(user).on(order.userId.eq(user.id))
            .where(*whereConditions)

        val results = selectClause(createBaseQuery())
            .offset(pageable.offset)
            .limit(pageable.pageSize.toLong())
            .orderBy(
                orderProduct.dateTimeFields.created.desc(),
                orderProduct.id.desc()
            )
            .fetch()

        val countQuery = {
            createBaseQuery()
                .select(orderProduct.count())
                .fetchOne() ?: 0L
        }

        return PageableExecutionUtils.getPage(
            results,
            pageable,
            countQuery,
        )
    }

    private fun getCommonWhereConditions(
        criteria: OrderProductSearchCriteria
    ): Array<BooleanExpression?> = arrayOf(
        eqOrderId(criteria.orderId),
        eqQuantity(criteria.quantity),
        goeStartDateTime(criteria.startDateTime),
        loeEndDateTime(criteria.endDateTime),
        eqIsActive(criteria.isActive),
        eqIsRemoved(criteria.isRemoved),
        eqOrderNumber(criteria.orderNumber),
        eqOrderStatus(criteria.orderStatus),
        eqOrderIsActive(criteria.orderIsActive),
        eqOrderIsRemoved(criteria.orderIsRemoved),
        eqProductName(criteria.productName),
        eqProductCode(criteria.productCode),
        eqProductStatus(criteria.productStatus),
        eqProductIsActive(criteria.productIsActive),
        eqProductIsRemoved(criteria.productIsRemoved),
        eqUserId(criteria.userId),
        eqUserEmail(criteria.userEmail),
        eqUserIsActive(criteria.userIsActive),
    )

    private fun eqId(
        orderProductId: Long?,
    ): BooleanExpression? =
        orderProductId?.let { orderProduct.id.eq(it) }

    private fun eqOrderId(
        orderId: Long?,
    ): BooleanExpression? =
        orderId?.let { orderProduct.orderId.eq(it) }

    private fun eqQuantity(
        quantity: Int?,
    ): BooleanExpression? =
        quantity?.let { orderProduct.quantity.eq(it) }

    private fun goeStartDateTime(
        startDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        startDateTime?.let { orderProduct.dateTimeFields.created.goe(it) }

    private fun loeEndDateTime(
        endDateTime: java.time.LocalDateTime?,
    ): BooleanExpression? =
        endDateTime?.let { orderProduct.dateTimeFields.created.loe(it) }

    private fun eqIsActive(
        isActive: Boolean?,
    ): BooleanExpression? =
        isActive?.let { orderProduct.removalFields.isRemoved.eq(!it) }

    private fun eqIsRemoved(
        isRemoved: Boolean?,
    ): BooleanExpression? =
        isRemoved?.let { orderProduct.removalFields.isRemoved.eq(it) }

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

    private fun eqProductName(
        productName: String?,
    ): BooleanExpression? =
        productName?.let { product.name.containsIgnoreCase(it) }

    private fun eqProductCode(
        productCode: String?,
    ): BooleanExpression? =
        productCode?.let { product.code.eq(it) }

    private fun eqProductStatus(
        productStatus: String?,
    ): BooleanExpression? =
        productStatus?.let { product.status.stringValue().eq(it) }

    private fun eqProductIsActive(
        productIsActive: Boolean?,
    ): BooleanExpression? =
        productIsActive?.let { product.removalFields.isRemoved.eq(!it) }

    private fun eqProductIsRemoved(
        productIsRemoved: Boolean?,
    ): BooleanExpression? =
        productIsRemoved?.let { product.removalFields.isRemoved.eq(it) }

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