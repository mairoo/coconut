package kr.co.pincoin.api.domain.order.enums

enum class OrderVisibility(val value: Int) {
    HIDDEN(0),
    VISIBLE(1);

    companion object {
        fun from(value: Int): OrderVisibility = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 주문 보기상태: $value")
    }
}