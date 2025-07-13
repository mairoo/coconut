package kr.pincoin.api.infra.order.repository

import com.querydsl.jpa.impl.JPAQueryFactory
import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import kr.pincoin.api.infra.order.entity.QOrderProductVoucherEntity
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherQueryRepositoryImpl(
    private val queryFactory: JPAQueryFactory,
) : OrderProductVoucherQueryRepository {
    private val orderProductVoucher = QOrderProductVoucherEntity.orderProductVoucherEntity

    override fun findById(
        id: Long,
    ): OrderProductVoucherEntity? =
        queryFactory
            .selectFrom(orderProductVoucher)
            .where(orderProductVoucher.id.eq(id))
            .fetchOne()
}