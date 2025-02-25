package kr.co.pincoin.api.domain.order.enums

enum class OrderStatus(val value: Int) {
    PAYMENT_PENDING(0),
    PAYMENT_COMPLETED(1),
    UNDER_REVIEW(2),
    PAYMENT_VERIFIED(3),
    SHIPPED(4),
    REFUND_REQUESTED(5),
    REFUND_PENDING(6),
    REFUNDED1(7),  // original order
    REFUNDED2(8),  // refund order
    VOIDED(9);

    companion object {
        fun from(value: Int): OrderStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 주문상태: $value")
    }
}
