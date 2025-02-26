package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrderPayment
import kr.co.pincoin.api.domain.inventory.repository.PurchaseOrderPaymentRepository
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderPaymentRepositoryImpl(
    private val jpaRepository: PurchaseOrderPaymentJpaRepository,
    private val queryRepository: PurchaseOrderPaymentQueryRepository,
) : PurchaseOrderPaymentRepository {
    override fun save(purchaseOrderPayment: PurchaseOrderPayment): PurchaseOrderPayment {
        TODO("Not yet implemented")
    }
}