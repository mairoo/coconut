package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.repository.OrderRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
) {
    @Transactional
    fun save(
        order: Order,
    ): Order {
        try {
            return orderRepository.save(order)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(OrderErrorCode.ORDER_SAVE_FAILED)
        }
    }

    fun findOrder(
        criteria: OrderSearchCriteria,
    ): Order? =
        orderRepository.findOrder(criteria)

    fun findOrderWithUserProfile(
        criteria: OrderSearchCriteria,
    ): OrderUserProfileProjection? =
        orderRepository.findOrderWithUserProfile(criteria)

    fun findOrders(
        criteria: OrderSearchCriteria,
    ): List<Order> =
        orderRepository.findOrders(criteria)

    fun findOrders(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<Order> =
        orderRepository.findOrders(criteria, pageable)

    fun findOrdersWithUserProfile(
        criteria: OrderSearchCriteria,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection> =
        orderRepository.findOrdersWithUserProfile(criteria, pageable)

    @Transactional
    fun updateStatus(
        id: Long,
        status: OrderStatus,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateStatus(status)
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun updateVisibility(
        id: Long,
        visibility: OrderVisibility,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateVisibility(visibility)
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun updateMessage(
        id: Long,
        message: String,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateMessage(message)
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun updateParent(
        id: Long,
        parentId: Long?,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateParent(parentId)
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun markAsSuspicious(
        id: Long,
        suspicious: Boolean = true,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsSuspicious(suspicious)
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun removeOrder(
        id: Long,
    ): Order =
        orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsRemoved()
            ?.let { orderRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    @Transactional
    fun restoreOrder(
        id: Long,
    ): Order {
        val order = orderRepository.findOrder(
            OrderSearchCriteria(
                id = id,
                isRemoved = true
            )
        )
            ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

        val restoredOrder = Order.of(
            id = order.id,
            created = order.created,
            modified = order.modified,
            isRemoved = false,
            orderNo = order.orderNo,
            userId = order.userId,
            fullname = order.fullname,
            userAgent = order.userAgent,
            acceptLanguage = order.acceptLanguage,
            ipAddress = order.ipAddress,
            paymentMethod = order.paymentMethod,
            transactionId = order.transactionId,
            status = order.status,
            visible = order.visible,
            totalListPrice = order.totalListPrice,
            totalSellingPrice = order.totalSellingPrice,
            currency = order.currency,
            message = order.message,
            parentId = order.parentId,
            suspicious = order.suspicious,
        )

        return save(restoredOrder)
    }
}