package kr.co.pincoin.api.domain.order.model

import java.math.BigDecimal
import java.time.ZonedDateTime

class OrderProduct private constructor(
    // 1. 공통 불변 필드
    val id: Long? = null,
    val created: ZonedDateTime? = null,
    val modified: ZonedDateTime? = null,

    // 2. 공통 가변 필드
    val isRemoved: Boolean = false,

    // 3. 도메인 로직 불변 필드
    val orderId: Long,
    val code: String,

    // 4. 도메인 로직 가변 필드
    val name: String,
    val subtitle: String,
    val listPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val quantity: Int,
) {
    fun update(
        newName: String? = null,
        newSubtitle: String? = null
    ): OrderProduct =
        copy(
            name = newName ?: name,
            subtitle = newSubtitle ?: subtitle
        )

    fun updatePrices(
        newListPrice: BigDecimal? = null,
        newSellingPrice: BigDecimal? = null
    ): OrderProduct =
        copy(
            listPrice = newListPrice ?: listPrice,
            sellingPrice = newSellingPrice ?: sellingPrice
        )

    fun updateQuantity(newQuantity: Int? = null): OrderProduct =
        copy(quantity = newQuantity ?: quantity)

    fun markAsRemoved(): OrderProduct =
        copy(isRemoved = true)

    private fun copy(
        name: String? = null,
        subtitle: String? = null,
        listPrice: BigDecimal? = null,
        sellingPrice: BigDecimal? = null,
        quantity: Int? = null,
        isRemoved: Boolean? = null
    ): OrderProduct = OrderProduct(
        id = this.id,
        created = this.created,
        modified = this.modified,
        isRemoved = isRemoved ?: this.isRemoved,
        orderId = this.orderId,
        code = this.code,
        name = name ?: this.name,
        subtitle = subtitle ?: this.subtitle,
        listPrice = listPrice ?: this.listPrice,
        sellingPrice = sellingPrice ?: this.sellingPrice,
        quantity = quantity ?: this.quantity
    )

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
                isRemoved = isRemoved ?: false,
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