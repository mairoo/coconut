package kr.co.pincoin.api.domain.order.enums

enum class OrderCurrency(val value: String) {
    KRW("KRW"),
    USD("USD");

    companion object {
        fun from(value: String): OrderCurrency = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 통화: $value")
    }
}