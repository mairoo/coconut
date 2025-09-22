package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.error.OrderProductErrorCode
import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.domain.order.repository.OrderProductRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderProductService(
    private val orderProductRepository: OrderProductRepository,
) {
    @Transactional
    fun save(
        orderProduct: OrderProduct,
    ): OrderProduct =
        try {
            orderProductRepository.save(orderProduct)
        } catch (_: DataIntegrityViolationException) {
            throw BusinessException(OrderProductErrorCode.ALREADY_EXISTS)
        }

    fun get(
        id: Long,
    ): OrderProduct =
        orderProductRepository.findById(id)
            ?: throw BusinessException(OrderProductErrorCode.NOT_FOUND)

    fun get(
        orderProductId: Long,
        criteria: OrderProductSearchCriteria,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(orderProductId, criteria)
            ?: throw BusinessException(OrderProductErrorCode.NOT_FOUND)

    fun get(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(criteria)
            ?: throw BusinessException(OrderProductErrorCode.NOT_FOUND)

    fun find(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProduct> =
        orderProductRepository.findOrderProducts(criteria, pageable)
}