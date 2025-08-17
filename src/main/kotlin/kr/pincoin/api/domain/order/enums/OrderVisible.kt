package kr.pincoin.api.domain.order.enums

enum class OrderVisible(val value: Int) {
    HIDDEN(0),
    VISIBLE(1);

    companion object {
        fun fromValue(value: Int): OrderVisible =
            OrderVisible.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid OrderVisible value: $value")
    }
}