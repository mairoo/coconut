package kr.co.pincoin.api.domain.order.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class OrderProduct private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    isRemoved: Boolean? = null,

    // 3. 도메인 로직 불변 필드
    val orderId: Long,
    val code: String,

    // 4. 도메인 로직 가변 필드
    name: String,
    subtitle: String,
    listPrice: BigDecimal,
    sellingPrice: BigDecimal,
    quantity: Int,
) {
    var isRemoved: Boolean = isRemoved ?: false
        private set

    var name: String = name
        private set

    var subtitle: String = subtitle
        private set

    var listPrice: BigDecimal = listPrice
        private set

    var sellingPrice: BigDecimal = sellingPrice
        private set

    var quantity: Int = quantity
        private set

    companion object {
        fun of(
            id: Long? = null,
            created: ZonedDateTime? = null,
            modified: ZonedDateTime? = null,
            isRemoved: Boolean? = null,
            orderId: Long,
            name: String,
            subtitle: String = "",
            code: String,
            listPrice: BigDecimal = BigDecimal.ZERO,
            sellingPrice: BigDecimal = BigDecimal.ZERO,
            quantity: Int = 0,
        ): OrderProduct =
            OrderProduct(
                id = id,
                created = created,
                modified = modified,
                isRemoved = isRemoved,
                orderId = orderId,
                name = name,
                subtitle = subtitle,
                code = code,
                listPrice = listPrice,
                sellingPrice = sellingPrice,
                quantity = quantity,
            )
    }
}