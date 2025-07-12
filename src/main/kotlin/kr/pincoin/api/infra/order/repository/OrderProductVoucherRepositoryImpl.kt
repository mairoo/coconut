package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherRepositoryImpl(
    private val jpaRepository: OrderProductVoucherJpaRepository,
    private val queryRepository: OrderProductVoucherQueryRepository,
) : OrderProductVoucherRepository {
}