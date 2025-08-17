package kr.pincoin.api.domain.order.enums

enum class OrderCurrency(val value: Int) {
    KRW(0),
    USD(1);

    companion object {
        fun fromValue(value: Int): OrderCurrency =
            OrderCurrency.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid OrderCurrency value: $value")
    }
}