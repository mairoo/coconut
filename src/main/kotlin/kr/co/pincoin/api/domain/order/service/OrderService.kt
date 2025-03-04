package kr.co.pincoin.api.domain.order.service

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.co.pincoin.api.app.order.member.request.CartItem
import kr.co.pincoin.api.app.order.member.request.CartOrderCreateRequest
import kr.co.pincoin.api.domain.catalog.enums.ProductStatus
import kr.co.pincoin.api.domain.catalog.enums.ProductStock
import kr.co.pincoin.api.domain.catalog.model.Product
import kr.co.pincoin.api.domain.catalog.repository.ProductRepository
import kr.co.pincoin.api.domain.order.enums.OrderCurrency
import kr.co.pincoin.api.domain.order.enums.OrderStatus
import kr.co.pincoin.api.domain.order.enums.OrderVisibility
import kr.co.pincoin.api.domain.order.model.Order
import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import kr.co.pincoin.api.domain.order.repository.OrderRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.CatalogErrorCode
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.global.utils.ClientUtils
import kr.co.pincoin.api.global.utils.IpUtils
import kr.co.pincoin.api.infra.catalog.repository.criteria.ProductSearchCriteria
import kr.co.pincoin.api.infra.order.repository.criteria.OrderSearchCriteria
import kr.co.pincoin.api.infra.order.repository.projection.OrderUserProfileProjection
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.*

@Service
@Transactional(readOnly = true)
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderProductRepository: OrderProductRepository,
    private val productRepository: ProductRepository,
) {
    private val log = KotlinLogging.logger {}

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

    @Transactional
    fun createOrderFromCart(
        userId: Int,
        request: CartOrderCreateRequest,
        servletRequest: HttpServletRequest,
    ): Order {
        val products = validateProductsForCartOrder(items = request.items)

        validateCartPrices(products, request.items)

        val clientInfo = ClientUtils.getClientInfo(servletRequest)

        // 총 금액 계산
        val totalListPrice = request.items.sumOf { it.listPrice * it.quantity.toBigDecimal() }
        val totalSellingPrice = request.items.sumOf { it.sellingPrice * it.quantity.toBigDecimal() }

        // 주문 생성
        val order = Order.of(
            isRemoved = false,
            orderNo = UUID.randomUUID(),
            userId = userId,
            fullname = "",
            userAgent = clientInfo.userAgent,
            acceptLanguage = clientInfo.acceptLanguage,
            ipAddress = IpUtils.parseInetAddress(clientInfo.ipAddress),
            paymentMethod = request.paymentMethod,
            transactionId = "",
            status = OrderStatus.PAYMENT_PENDING,
            visible = OrderVisibility.VISIBLE,
            totalListPrice = totalListPrice,
            totalSellingPrice = totalSellingPrice,
            currency = OrderCurrency.KRW,
            message = "",
            parentId = null,
            suspicious = false,
        )

        // 주문 저장
        val savedOrder = orderRepository.saveAndFlush(order)

        // 주문 상품 생성
        val orderProducts = request.items.map { item ->
            OrderProduct.of(
                orderId = savedOrder.id!!,
                name = item.name,
                subtitle = item.subtitle,
                code = item.code,
                listPrice = item.listPrice,
                sellingPrice = item.sellingPrice,
                quantity = item.quantity,
            )
        }

        // 주문 상품 저장
        orderProductRepository.saveAll(orderProducts)

        return savedOrder
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

    private fun validateProductsForCartOrder(items: List<CartItem>): List<Product> {
        // 장바구니에 담긴 상품 코드 추출
        val requestedCodes = items.map { it.code }.toSet()

        // 데이터베이스에서 실제 상품 조회
        val products = productRepository.findProducts(
            ProductSearchCriteria(
                codes = requestedCodes.toList(),
                status = ProductStatus.ENABLED,
                stock = ProductStock.IN_STOCK,
            )
        )

        // 존재하지 않는 상품 코드 확인
        val foundCodes = products.map { it.code }.toSet()

        requestedCodes
            .minus(foundCodes)
            .takeIf { it.isNotEmpty() }
            ?.let { notFoundCodes ->
                throw BusinessException(
                    CatalogErrorCode.PRODUCT_NOT_FOUND,
                    notFoundCodes.joinToString(", ")
                )
            }

        // 수량별 상품 코드 집계
        val quantityByCode = items
            .groupBy { it.code }
            .mapValues { (_, items) -> items.sumOf { it.quantity } }

        // 상품 유효성 검사
        val errors = products.mapNotNull { product ->
            val requestedQuantity = quantityByCode[product.code] ?: 0
            when {
                product.status == ProductStatus.DISABLED || product.stock == ProductStock.SOLD_OUT ->
                    "판매 중인 상품이 아닙니다: ${product.code}"

                product.stockQuantity < requestedQuantity ->
                    "상품 '${product.name}'의 재고가 부족합니다. 요청: $requestedQuantity, 재고: ${product.stockQuantity}"

                else -> null
            }
        }

        if (errors.isNotEmpty()) {
            log.warn { errors.joinToString("\n") }
            throw BusinessException(
                OrderErrorCode.ORDER_OUT_OF_STOCK,
                errors.joinToString(", ")
            )
        }

        return products
    }

    private fun validateCartPrices(products: List<Product>, items: List<CartItem>) {
        val productMap = products.associateBy { it.id }

        items
            .mapNotNull { item ->
                productMap[item.productId]
                    ?.takeIf { product ->
                        product.sellingPrice.compareTo(item.sellingPrice) != 0
                    }
                    ?.let { product ->
                        "상품 '${product.name}'의 가격이 변경되었습니다. 장바구니: ${item.sellingPrice}, 실제: ${product.sellingPrice}"
                    }
            }
            .takeIf { it.isNotEmpty() }
            ?.let { priceErrors ->
                log.warn { priceErrors.joinToString("\n") }
                throw BusinessException(
                    OrderErrorCode.ORDER_PRICE_MISMATCH,
                    priceErrors.joinToString(", ")
                )
            }
    }
}