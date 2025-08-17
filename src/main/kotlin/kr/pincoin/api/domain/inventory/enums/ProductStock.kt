package kr.pincoin.api.domain.inventory.enums

enum class ProductStock(val value: Int) {
    SOLD_OUT(0),
    IN_STOCK(1);

    companion object {
        fun fromValue(value: Int) =
            ProductStock.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Unknown ProductStock value $value")
    }
}
