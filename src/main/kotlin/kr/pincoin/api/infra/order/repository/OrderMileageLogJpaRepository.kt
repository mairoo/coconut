package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.infra.order.entity.OrderMileageLogEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OrderMileageLogJpaRepository : JpaRepository<OrderMileageLogEntity, Long> {
}