package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.model.OrderProductVoucher
import kr.co.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderProductVoucherService(
    private val orderProductVoucherRepository: OrderProductVoucherRepository,
) {
    @Transactional
    fun save(
        orderProductVoucher: OrderProductVoucher,
    ): OrderProductVoucher {
        try {
            return orderProductVoucherRepository.save(orderProductVoucher)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(OrderErrorCode.ORDER_PRODUCT_VOUCHER_SAVE_FAILED)
        }
    }

    @Transactional
    fun saveAll(
        orderProductVouchers: List<OrderProductVoucher>,
    ): List<OrderProductVoucher> =
        orderProductVoucherRepository.saveAll(orderProductVouchers)

    fun findOrderProductVoucher(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher? =
        orderProductVoucherRepository.findOrderProductVoucher(criteria)

    @Transactional
    fun updateRevoked(
        id: Long,
        revoked: Boolean,
    ): OrderProductVoucher =
        orderProductVoucherRepository.findOrderProductVoucher(
            OrderProductVoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateRevoked(revoked)
            ?.let { orderProductVoucherRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_VOUCHER_NOT_FOUND)

    @Transactional
    fun removeOrderProductVoucher(
        id: Long,
    ): OrderProductVoucher =
        orderProductVoucherRepository.findOrderProductVoucher(
            OrderProductVoucherSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsRemoved()
            ?.let { orderProductVoucherRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_VOUCHER_NOT_FOUND)

    @Transactional
    fun restoreOrderProductVoucher(
        id: Long,
    ): OrderProductVoucher {
        val orderProductVoucher = orderProductVoucherRepository.findOrderProductVoucher(
            OrderProductVoucherSearchCriteria(
                id = id,
                isRemoved = true
            )
        ) ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_VOUCHER_NOT_FOUND)

        val restoredOrderProductVoucher = OrderProductVoucher.of(
            id = orderProductVoucher.id,
            created = orderProductVoucher.created,
            modified = orderProductVoucher.modified,
            isRemoved = false,
            orderProductId = orderProductVoucher.orderProductId,
            voucherId = orderProductVoucher.voucherId,
            code = orderProductVoucher.code,
            revoked = orderProductVoucher.revoked,
            remarks = orderProductVoucher.remarks,
        )

        return save(restoredOrderProductVoucher)
    }
}