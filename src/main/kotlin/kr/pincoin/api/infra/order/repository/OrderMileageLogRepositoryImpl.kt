package kr.pincoin.api.infra.order.repository

import kr.pincoin.api.domain.order.repository.OrderMileageLogRepository
import org.springframework.stereotype.Repository

@Repository
class OrderMileageLogRepositoryImpl(
    private val jpaRepository: OrderMileageLogJpaRepository
) : OrderMileageLogRepository {
}