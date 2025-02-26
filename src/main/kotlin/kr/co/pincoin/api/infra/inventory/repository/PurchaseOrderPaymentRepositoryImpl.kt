package kr.co.pincoin.api.infra.inventory.repository

import kr.co.pincoin.api.domain.inventory.model.PurchaseOrderPayment
import kr.co.pincoin.api.domain.inventory.repository.PurchaseOrderPaymentRepository
import kr.co.pincoin.api.infra.inventory.mapper.toEntity
import kr.co.pincoin.api.infra.inventory.mapper.toModel
import org.springframework.stereotype.Repository

@Repository
class PurchaseOrderPaymentRepositoryImpl(
    private val jpaRepository: PurchaseOrderPaymentJpaRepository,
    private val queryRepository: PurchaseOrderPaymentQueryRepository,
) : PurchaseOrderPaymentRepository {
    override fun save(
        purchaseOrderPayment: PurchaseOrderPayment,
    ): PurchaseOrderPayment =
        purchaseOrderPayment.toEntity()
            ?.let { jpaRepository.save(it) }
            ?.toModel()
            ?: throw IllegalArgumentException("매입주문결제 저장 실패")
}