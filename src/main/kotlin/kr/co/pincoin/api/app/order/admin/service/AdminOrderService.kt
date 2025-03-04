package kr.co.pincoin.api.app.order.admin.service

import kr.co.pincoin.api.app.order.admin.request.OrderSearchRequest
import kr.co.pincoin.api.domain.order.model.OrderPayment
import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.domain.order.service.OrderPaymentService
import kr.co.pincoin.api.domain.order.service.OrderProductService
import kr.co.pincoin.api.domain.order.service.OrderProductVoucherService
import kr.co.pincoin.api.domain.order.service.OrderService
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderPaymentSearchCriteria
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductVoucherSearchCriteria
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderProductVoucherProjection
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('ADMIN')")
class AdminOrderService(
    private val orderService: OrderService,
    private val orderPaymentService: OrderPaymentService,
    private val orderProductService: OrderProductService,
    private val orderProductVoucherService: OrderProductVoucherService,
) {
    fun getOrderWithUserProfile(
        id: Long,
        request: OrderSearchRequest,
    ): OrderUserProfileProjection =
        orderService.findOrderWithUserProfile(
            OrderSearchCriteria(
                id = id,
                orderNo = request.orderNo,
                userId = request.userId,
                fullname = request.fullname,
                ipAddress = request.ipAddress,
                paymentMethod = request.paymentMethod,
                transactionId = request.transactionId,
                status = request.status,
                visible = request.visible,
                currency = request.currency,
                parentId = request.parentId,
                suspicious = request.suspicious,
                isRemoved = request.isRemoved,
            )
        ) ?: throw BusinessException(OrderErrorCode.ORDER_NOT_FOUND)

    fun getOrdersWithUserProfile(
        request: OrderSearchRequest,
        pageable: Pageable,
    ): Page<OrderUserProfileProjection> =
        orderService.findOrdersWithUserProfile(
            OrderSearchCriteria(
                id = request.orderId,
                orderNo = request.orderNo,
                userId = request.userId,
                fullname = request.fullname,
                ipAddress = request.ipAddress,
                paymentMethod = request.paymentMethod,
                transactionId = request.transactionId,
                status = request.status,
                visible = request.visible,
                currency = request.currency,
                parentId = request.parentId,
                suspicious = request.suspicious,
                isRemoved = request.isRemoved,
            ),
            pageable
        )

    fun getOrderPayments(
        orderId: Long,
    ): List<OrderPayment> =
        orderPaymentService.findOrderPayments(
            OrderPaymentSearchCriteria(
                orderId = orderId,
            )
        )

    fun getOrderItems(
        orderId: Long,
    ): List<OrderProduct> =
        orderProductService.findOrderProducts(
            OrderProductSearchCriteria(
                orderId = orderId,
            )
        )

    fun getOrderVouchers(
        orderId: Long,
    ): List<OrderProductVoucherProjection> =
        orderProductVoucherService.findOrderProductVouchersWithProduct(
            OrderProductVoucherSearchCriteria(
                orderId = orderId,
            )
        )
}