package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.repository.PurchaseOrderRepository
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderRepositoryImpl(
    private val jpaRepository: PurchaseOrderJpaRepository,
    private val queryRepository: PurchaseOrderQueryRepository,
) : PurchaseOrderRepository {
}