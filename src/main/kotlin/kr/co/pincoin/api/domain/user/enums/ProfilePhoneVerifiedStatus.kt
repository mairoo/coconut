package kr.co.pincoin.api.domain.user.enums

enum class ProfilePhoneVerifiedStatus(val value: Int) {
    UNVERIFIED(0),
    VERIFIED(1),
    REVOKED(2);

    companion object {
        fun from(value: Int): ProfilePhoneVerifiedStatus = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 휴대폰인증 상태: $value")
    }
}