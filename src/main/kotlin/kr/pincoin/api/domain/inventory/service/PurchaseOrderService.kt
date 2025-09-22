package kr.pincoin.api.domain.inventory.service

import kr.pincoin.api.domain.inventory.error.PurchaseOrderErrorCode
import kr.pincoin.api.domain.inventory.model.PurchaseOrder
import kr.pincoin.api.domain.inventory.repository.PurchaseOrderRepository
import kr.pincoin.api.global.exception.BusinessException
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class PurchaseOrderService(
    private val purchaseOrderRepository: PurchaseOrderRepository,
) {
    @Transactional
    fun save(
        purchaseOrder: PurchaseOrder,
    ): PurchaseOrder =
        try {
            purchaseOrderRepository.save(purchaseOrder)
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(PurchaseOrderErrorCode.ALREADY_EXISTS)
        }

    fun get(
        id: Long,
    ): PurchaseOrder =
        purchaseOrderRepository.findById(id)
            ?: throw BusinessException(PurchaseOrderErrorCode.NOT_FOUND)
}