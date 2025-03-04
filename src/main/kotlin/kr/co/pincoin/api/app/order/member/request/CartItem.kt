package kr.co.pincoin.api.app.order.member.request

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import kr.co.pincoin.api.domain.order.model.OrderProduct
import java.math.BigDecimal

data class CartItem(
    @field:NotNull(message = "상품 ID는 필수입니다")
    val productId: Long,

    @field:NotNull(message = "상품명은 필수입니다")
    val name: String,

    @field:NotNull(message = "상품부제는 필수입니다")
    val subtitle: String = "",

    @field:NotNull(message = "상품 코드는 필수입니다")
    val code: String,

    @field:NotNull(message = "정가 필수입니다")
    val listPrice: BigDecimal,

    @field:NotNull(message = "판매가 필수입니다")
    val sellingPrice: BigDecimal,

    @field:NotNull(message = "주문 수량은 필수입니다")
    @field:Min(value = 1, message = "주문 수량은 1개 이상이어야 합니다")
    val quantity: Int
) {
    companion object {
        fun from(orderProduct: OrderProduct): CartItem {
            requireNotNull(orderProduct.id)

            return CartItem(
                productId = orderProduct.id,
                name = orderProduct.name,
                subtitle = orderProduct.subtitle,
                code = orderProduct.code,
                listPrice = orderProduct.listPrice,
                sellingPrice = orderProduct.sellingPrice,
                quantity = orderProduct.quantity
            )
        }
    }
}