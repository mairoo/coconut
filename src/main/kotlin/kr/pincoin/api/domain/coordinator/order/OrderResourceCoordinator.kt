package kr.pincoin.api.domain.coordinator.order

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.order.member.request.MemberOrderCreateRequest
import kr.pincoin.api.domain.inventory.service.VoucherService
import kr.pincoin.api.domain.order.enums.OrderStatus
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.domain.order.service.OrderProductService
import kr.pincoin.api.domain.order.service.OrderService
import kr.pincoin.api.global.utils.IpUtils
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class OrderResourceCoordinator(
    private val orderService: OrderService,
    private val orderProductService: OrderProductService,
    private val orderProductVoucherService: VoucherService,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createOrder(
        userId: Int,
        request: MemberOrderCreateRequest,
        httpRequest: HttpServletRequest,
    ): Order {
        try {
            // TODO: totalListPrice, totalSellingPrice 서버 측 재계산

            // 1. Order 생성
            val savedOrder = orderService.save(
                Order.of(
                    orderNo = UUID.randomUUID(),
                    status = OrderStatus.PAYMENT_PENDING,
                    totalListPrice = request.totalAmount,
                    totalSellingPrice = request.totalAmount,
                    paymentMethod = request.paymentMethod,
                    fullname = "",
                    ipAddress = IpUtils.getClientIp(httpRequest),
                    userAgent = httpRequest.getHeader("User-Agent") ?: "",
                    acceptLanguage = httpRequest.getHeader("Accept-Language") ?: "",
                    userId = userId,
                )
            )

            // 2. OrderProduct 생성
            orderProductService.saveAll(request.products.map { productRequest ->
                OrderProduct.of(
                    orderId = savedOrder.id!!,
                    name = productRequest.title,
                    subtitle = productRequest.subtitle,
                    code = productRequest.id,
                    listPrice = productRequest.price,
                    sellingPrice = productRequest.price,
                    quantity = productRequest.quantity,
                )
            })

            return savedOrder
        } catch (e: Exception) {
            logger.error { "주문 생성 실패: userId=$userId, error=${e.message}" }
            throw e
        }
    }
}