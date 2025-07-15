package kr.pincoin.api.domain.inventory.enums

enum class VoucherStatus(val value: Int) {
    PURCHASED(0),
    SOLD(1),
    REVOKED(2);

    companion object {
        fun fromValue(value: Int): VoucherStatus =
            VoucherStatus.entries.find { it.value == value }
                ?: throw IllegalArgumentException("Invalid VoucherStatus value: $value")
    }
}