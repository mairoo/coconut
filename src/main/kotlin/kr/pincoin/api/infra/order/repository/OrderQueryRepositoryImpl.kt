package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderEntity
import kr.pincoin.api.infra.order.entity.QOrderEntity
import org.springframework.stereotype.Repository

@Repository
class OrderQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderQueryRepository {
    private val order = QOrderEntity.orderEntity

    override fun findById(
        id: Long,
    ): OrderEntity? =
        queryFactory
            .selectFrom(order)
            .where(order.id.eq(id))
            .fetchOne()
}