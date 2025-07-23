package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderPaymentEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderPaymentJpaRepository : JpaRepository<OrderPaymentEntity, Long> {}