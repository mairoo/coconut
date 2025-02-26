package kr.co.pincoin.api.domain.catalog.enums

enum class ProductStock(val value: Int) {
    SOLD_OUT(0),
    IN_STOCK(1);

    companion object {
        fun from(value: Int): ProductStock = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 재고 상태")
    }
}