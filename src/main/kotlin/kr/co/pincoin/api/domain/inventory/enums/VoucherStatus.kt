package kr.co.pincoin.api.domain.inventory.enums

enum class VoucherStatus(val value: Int) {
    PURCHASED(0), // 매입
    SOLD(1), // 판매
    REVOKED(2); // 취소

    companion object {
        fun from(value: Int): VoucherStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 상품권 상태 '$value'")
    }
}