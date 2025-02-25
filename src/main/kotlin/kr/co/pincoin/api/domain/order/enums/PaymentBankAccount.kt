package kr.co.pincoin.api.domain.order.enums

enum class PaymentBankAccount(val value: Int) {
    KB(0),
    NH(1),
    SHINHAN(2),
    WOORI(3),
    IBK(4),
    PAYPAL(5);

    companion object {
        fun from(value: Int): PaymentBankAccount = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 입금은행: $value")
    }
}