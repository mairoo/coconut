package kr.pincoin.api.domain.order.service

import kr.pincoin.api.domain.order.error.OrderErrorCode
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.repository.OrderRepository
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
) {
    fun findOrder(
        orderId: Long,
        criteria: OrderSearchCriteria,
    ): Order =
        orderRepository.findOrder(orderId, criteria)
            ?: throw BusinessException(OrderErrorCode.NOT_FOUND)

    fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order =
        orderRepository.findOrder(criteria)
            ?: throw BusinessException(OrderErrorCode.NOT_FOUND)

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<Order> =
        orderRepository.findOrders(criteria)
}