package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import org.springframework.stereotype.Repository

@Repository
class OrderProductVoucherRepositoryImpl(
    private val jpaRepository: OrderProductVoucherJpaRepository,
) : OrderProductVoucherRepository {
}