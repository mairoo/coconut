package kr.co.pincoin.api.app.order.member.request

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import kr.co.pincoin.api.domain.order.enums.OrderPaymentMethod

data class CartOrderCreateRequest(
    @field:NotNull(message = "주문 상품 목록은 필수입니다.")
    @field:NotEmpty(message = "최소 1개 이상의 상품을 주문해야 합니다.")
    @field:Valid
    val items: List<CartItem>,

    @field:NotNull(message = "결제 방법은 필수입니다.")
    val paymentMethod: OrderPaymentMethod,
)