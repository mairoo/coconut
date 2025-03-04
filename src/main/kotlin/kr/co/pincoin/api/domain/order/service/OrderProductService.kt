package kr.co.pincoin.api.domain.order.service

import kr.co.pincoin.api.domain.order.model.OrderProduct
import kr.co.pincoin.api.domain.order.repository.OrderProductRepository
import kr.co.pincoin.api.global.exception.BusinessException
import kr.co.pincoin.api.global.exception.code.OrderErrorCode
import kr.co.pincoin.api.infra.order.repository.criteria.OrderProductSearchCriteria
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
@Transactional(readOnly = true)
class OrderProductService(
    private val orderProductRepository: OrderProductRepository,
) {
    @Transactional
    fun save(
        orderProduct: OrderProduct,
    ): OrderProduct {
        try {
            return orderProductRepository.save(orderProduct)
        } catch (e: DataIntegrityViolationException) {
            throw BusinessException(OrderErrorCode.ORDER_PRODUCT_SAVE_FAILED)
        }
    }

    @Transactional
    fun saveAll(
        orderProducts: List<OrderProduct>,
    ): List<OrderProduct> =
        orderProductRepository.saveAll(orderProducts)

    fun findOrderProduct(
        criteria: OrderProductSearchCriteria,
    ): OrderProduct? = orderProductRepository.findOrderProduct(criteria)

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
    ): List<OrderProduct> = orderProductRepository.findOrderProducts(criteria)

    fun findOrderProducts(
        criteria: OrderProductSearchCriteria,
        pageable: Pageable,
    ): Page<OrderProduct> = orderProductRepository.findOrderProducts(criteria, pageable)

    fun findOrderProductsByOrderId(
        orderId: Long,
        isRemoved: Boolean = false,
    ): List<OrderProduct> = orderProductRepository.findOrderProducts(
        OrderProductSearchCriteria(
            orderId = orderId,
            isRemoved = isRemoved
        )
    )

    @Transactional
    fun update(
        id: Long,
        name: String? = null,
        subtitle: String? = null,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(
            OrderProductSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.update(name, subtitle)
            ?.let { orderProductRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND)

    @Transactional
    fun updatePrices(
        id: Long,
        listPrice: BigDecimal? = null,
        sellingPrice: BigDecimal? = null,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(
            OrderProductSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updatePrices(listPrice, sellingPrice)
            ?.let { orderProductRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND)

    @Transactional
    fun updateQuantity(
        id: Long,
        quantity: Int,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(
            OrderProductSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.updateQuantity(quantity)
            ?.let { orderProductRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND)

    @Transactional
    fun removeOrderProduct(
        id: Long,
    ): OrderProduct =
        orderProductRepository.findOrderProduct(
            OrderProductSearchCriteria(
                id = id,
                isRemoved = false
            )
        )
            ?.markAsRemoved()
            ?.let { orderProductRepository.save(it) }
            ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND)

    @Transactional
    fun restoreOrderProduct(
        id: Long,
    ): OrderProduct {
        val orderProduct = orderProductRepository.findOrderProduct(
            OrderProductSearchCriteria(
                id = id,
                isRemoved = true
            )
        ) ?: throw BusinessException(OrderErrorCode.ORDER_PRODUCT_NOT_FOUND)

        val restoredOrderProduct = OrderProduct.of(
            id = orderProduct.id,
            created = orderProduct.created,
            modified = orderProduct.modified,
            isRemoved = false,
            orderId = orderProduct.orderId,
            name = orderProduct.name,
            subtitle = orderProduct.subtitle,
            code = orderProduct.code,
            listPrice = orderProduct.listPrice,
            sellingPrice = orderProduct.sellingPrice,
            quantity = orderProduct.quantity,
        )

        return save(restoredOrderProduct)
    }

    @Transactional
    fun removeOrderProducts(
        ids: List<Long>,
    ): List<OrderProduct> {
        if (ids.isEmpty()) {
            return emptyList()
        }

        val orderProducts = ids.mapNotNull { id ->
            orderProductRepository.findOrderProduct(
                OrderProductSearchCriteria(id = id, isRemoved = false)
            )
        }

        if (orderProducts.isEmpty()) {
            return emptyList()
        }

        val removedOrderProducts = orderProducts.map { it.markAsRemoved() }
        return saveAll(removedOrderProducts)
    }

    @Transactional
    fun removeOrderProductsByOrderId(
        orderId: Long,
    ): List<OrderProduct> {
        val orderProducts = orderProductRepository.findOrderProducts(
            OrderProductSearchCriteria(
                orderId = orderId,
                isRemoved = false
            )
        )

        if (orderProducts.isEmpty()) {
            return emptyList()
        }

        val removedOrderProducts = orderProducts.map { it.markAsRemoved() }
        return saveAll(removedOrderProducts)
    }
}