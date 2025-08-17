package kr.pincoin.api.domain.inventory.enums

enum class ProductStatus(val value: Int) {
    ENABLED(0),
    DISABLED(1);

    companion object {
        fun fromValue(value: Int): ProductStatus =
            ProductStatus.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid ProductStatus value: $value")
    }
}
