package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderPaymentEntity
import kr.pincoin.api.infra.order.entity.QOrderPaymentEntity
import org.springframework.stereotype.Repository

@Repository
class OrderPaymentQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderPaymentQueryRepository {
    private val orderPayment = QOrderPaymentEntity.orderPaymentEntity

    override fun findById(
        id: Long,
    ): OrderPaymentEntity? =
        queryFactory
            .selectFrom(orderPayment)
            .where(orderPayment.id.eq(id))
            .fetchOne()
}