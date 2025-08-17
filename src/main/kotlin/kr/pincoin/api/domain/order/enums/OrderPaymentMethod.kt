package kr.pincoin.api.domain.order.enums

enum class OrderPaymentMethod(val value: Int) {
    BANK_TRANSFER(0),
    ESCROW(1),
    PAYPAL(2),
    CREDIT_CARD(3),
    BANK_TRANSFER_PG(4),
    VIRTUAL_ACCOUNT(5),
    PHONE_BILL(6);

    companion object {
        fun fromValue(value: Int): OrderPaymentMethod =
            OrderPaymentMethod.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid OrderPaymentMethod value: $value")
    }
}