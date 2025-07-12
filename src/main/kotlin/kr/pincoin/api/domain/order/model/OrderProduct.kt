package kr.pincoin.api.domain.order.model

import java.math.BigDecimal
import java.time.LocalDateTime

class OrderProduct private constructor(
    val id: Long? = null,
    val created: LocalDateTime? = null,
    val modified: LocalDateTime? = null,
    val isRemoved: Boolean = false,
    val name: String,
    val subtitle: String = "",
    val code: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val quantity: Int = 1,
    val orderId: Long,
) {
    companion object {
        fun of(
            id: Long? = null,
            created: LocalDateTime? = null,
            modified: LocalDateTime? = null,
            isRemoved: Boolean = false,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal,
            sellingPrice: BigDecimal,
            quantity: Int = 1,
            orderId: Long,
        ): OrderProduct = OrderProduct(
            id = id,
            created = created,
            modified = modified,
            isRemoved = isRemoved,
            name = name,
            subtitle = subtitle,
            code = code,
            listPrice = listPrice,
            sellingPrice = sellingPrice,
            quantity = quantity,
            orderId = orderId,
        )
    }
}