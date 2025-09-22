package kr.pincoin.api.domain.coordinator.order

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import kr.pincoin.api.app.order.member.request.MemberOrderCreateRequest
import kr.pincoin.api.domain.inventory.enums.ProductStatus
import kr.pincoin.api.domain.inventory.enums.ProductStock
import kr.pincoin.api.domain.inventory.error.ProductErrorCode
import kr.pincoin.api.domain.inventory.service.ProductService
import kr.pincoin.api.domain.inventory.service.VoucherService
import kr.pincoin.api.domain.order.enums.OrderStatus
import kr.pincoin.api.domain.order.model.Order
import kr.pincoin.api.domain.order.model.OrderProduct
import kr.pincoin.api.domain.order.service.OrderProductService
import kr.pincoin.api.domain.order.service.OrderService
import kr.pincoin.api.global.exception.BusinessException
import kr.pincoin.api.global.utils.IpUtils
import kr.pincoin.api.infra.inventory.repository.criteria.ProductSearchCriteria
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.util.*

@Service
@Transactional(readOnly = true)
class OrderResourceCoordinator(
    private val orderService: OrderService,
    private val orderProductService: OrderProductService,
    private val orderProductVoucherService: VoucherService,
    private val productService: ProductService,
) {
    private val logger = KotlinLogging.logger {}

    @Transactional
    fun createOrder(
        userId: Int,
        request: MemberOrderCreateRequest,
        httpRequest: HttpServletRequest,
    ): Order {
        try {
            // 1. 서버 측에서 상품 정보 및 가격 검증 (단일 쿼리로 최적화)
            val productCodes = request.products.map { it.id }
            val products = productService.find(
                criteria = ProductSearchCriteria(
                    codes = productCodes,
                    isRemoved = false,
                )
            )

            // 2. 요청된 모든 상품이 존재하는지 확인
            val productMap = products.associateBy { it.code }
            val validatedProducts = request.products.map { productRequest ->
                val product = productMap[productRequest.id]
                    ?: throw BusinessException(ProductErrorCode.NOT_FOUND)

                // 상품 상태 검증
                when {
                    product.status != ProductStatus.ENABLED -> {
                        throw BusinessException(ProductErrorCode.DISABLED)
                    }

                    product.stock != ProductStock.IN_STOCK -> {
                        throw BusinessException(ProductErrorCode.OUT_OF_STOCK)
                    }
                }

                // 클라이언트가 보낸 가격과 실제 DB 가격 비교 (보안상 클라이언트 가격 무시)
                Triple(product, productRequest.quantity, productRequest)
            }

            // 3. 서버 측에서 총 금액 재계산
            val calculatedTotalListPrice = validatedProducts.sumOf { (product, quantity, _) ->
                product.listPrice.multiply(BigDecimal.valueOf(quantity.toLong()))
            }
            val calculatedTotalSellingPrice = validatedProducts.sumOf { (product, quantity, _) ->
                product.sellingPrice.multiply(BigDecimal.valueOf(quantity.toLong()))
            }

            // 4. Order 생성
            val savedOrder = orderService.save(
                Order.of(
                    orderNo = UUID.randomUUID(),
                    status = OrderStatus.PAYMENT_PENDING,
                    totalListPrice = calculatedTotalListPrice,
                    totalSellingPrice = calculatedTotalSellingPrice,
                    paymentMethod = request.paymentMethod,
                    fullname = "",
                    ipAddress = IpUtils.getClientIp(httpRequest),
                    userAgent = httpRequest.getHeader("User-Agent") ?: "",
                    acceptLanguage = httpRequest.getHeader("Accept-Language") ?: "",
                    userId = userId,
                )
            )

            // 5. OrderProduct 생성 (서버에서 검증된 가격 사용)
            orderProductService.saveAll(validatedProducts.map { (product, quantity, _) ->
                OrderProduct.of(
                    orderId = savedOrder.id!!,
                    name = product.name,
                    subtitle = product.subtitle,
                    code = product.code,
                    listPrice = product.listPrice,
                    sellingPrice = product.sellingPrice,
                    quantity = quantity,
                )
            })

            return savedOrder
        } catch (e: Exception) {
            logger.error { "주문 생성 실패: userId=$userId, error=${e.message}" }
            throw e
        }
    }
}