package kr.co.pincoin.api.domain.inventory.enums

enum class VoucherStatus(val value: Int) {
    PURCHASED(0), // 매입
    SOLD(1), // 판매
    REVOKED(2); // 취소
}