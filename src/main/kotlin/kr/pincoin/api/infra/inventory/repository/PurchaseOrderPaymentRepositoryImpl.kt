package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.repository.PurchaseOrderPaymentRepository
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderPaymentRepositoryImpl(
    private val jpaRepository: PurchaseOrderPaymentJpaRepository,
    private val queryRepository: PurchaseOrderPaymentQueryRepository,
) : PurchaseOrderPaymentRepository {
}