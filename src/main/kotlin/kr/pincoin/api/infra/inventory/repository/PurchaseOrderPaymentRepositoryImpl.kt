package kr.pincoin.api.infra.inventory.repository

import kr.pincoin.api.domain.inventory.model.PurchaseOrderPayment
import kr.pincoin.api.domain.inventory.repository.PurchaseOrderPaymentRepository
import kr.pincoin.api.infra.inventory.mapper.toEntity
import kr.pincoin.api.infra.inventory.mapper.toModel
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
            ?: throw IllegalArgumentException("주문발주입금 저장 실패")

    override fun findById(
        id: Long,
    ): PurchaseOrderPayment? =
        queryRepository.findById(id)?.toModel()
}