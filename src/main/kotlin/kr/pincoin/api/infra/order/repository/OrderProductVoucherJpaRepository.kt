package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderProductVoucherEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderProductVoucherJpaRepository : JpaRepository<OrderProductVoucherEntity, Long> {
}