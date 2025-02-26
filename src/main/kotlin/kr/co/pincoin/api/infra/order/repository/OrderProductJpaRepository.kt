package kr.co.pincoin.api.infra.order.repository

import kr.co.pincoin.api.infra.order.entity.OrderProductEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderProductJpaRepository : JpaRepository<OrderProductEntity, Long>