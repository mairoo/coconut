package kr.co.pincoin.api.domain.catalog.enums

enum class ProductStatus(val value: Int) {
    ENABLED(0),
    DISABLED(1);

    companion object {
        fun from(value: Int): ProductStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 상품 상태")
    }
}