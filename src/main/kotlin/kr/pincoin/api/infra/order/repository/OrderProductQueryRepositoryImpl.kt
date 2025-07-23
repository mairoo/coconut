package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderProductEntity
import kr.pincoin.api.infra.order.entity.QOrderProductEntity
import org.springframework.stereotype.Repository

@Repository
class OrderProductQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductQueryRepository {
    private val orderProduct = QOrderProductEntity.orderProductEntity

    override fun findById(
        id: Long,
    ): OrderProductEntity? =
        queryFactory
            .selectFrom(orderProduct)
            .where(orderProduct.id.eq(id))
            .fetchOne()
}