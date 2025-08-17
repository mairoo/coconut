package kr.pincoin.api.domain.order.enums

enum class OrderStatus(val value: Int) {
    PAYMENT_PENDING(0),
    PAYMENT_COMPLETED(1),
    UNDER_REVIEW(2),
    PAYMENT_VERIFY(3),
    SHIPPED(4),
    REFUND_REQUESTED(5),
    REFUND_PENDING(6),
    REFUNDED1(7),
    REFUNDED2(8),
    VOIDED(9);

    companion object {
        fun fromValue(value: Int): OrderStatus =
            OrderStatus.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid OrderStatus $value")
    }
}