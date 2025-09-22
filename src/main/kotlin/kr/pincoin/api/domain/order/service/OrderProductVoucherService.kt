package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.error.OrderProductVoucherErrorCode
import kr.pincoin.api.domain.order.model.OrderProductVoucher
import kr.pincoin.api.domain.order.repository.OrderProductVoucherRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
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
    ): OrderProductVoucher =
        try {
            orderProductVoucherRepository.save(orderProductVoucher)
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(OrderProductVoucherErrorCode.ALREADY_EXISTS)
        }

    fun get(
        id: Long,
    ): OrderProductVoucher =
        orderProductVoucherRepository.findById(id)
            ?: throw BusinessException(OrderProductVoucherErrorCode.NOT_FOUND)

    fun get(
        voucherId: Long,
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher =
        orderProductVoucherRepository.findOrderProductVoucher(voucherId, criteria)
            ?: throw BusinessException(OrderProductVoucherErrorCode.NOT_FOUND)

    fun get(
        criteria: OrderProductVoucherSearchCriteria,
    ): OrderProductVoucher =
        orderProductVoucherRepository.findOrderProductVoucher(criteria)
            ?: throw BusinessException(OrderProductVoucherErrorCode.NOT_FOUND)

    fun find(
        criteria: OrderProductVoucherSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProductVoucher> =
        orderProductVoucherRepository.findOrderProductVouchers(criteria, pageable)
}