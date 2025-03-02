package kr.co.pincoin.api.domain.user.enums

enum class ProfileDomestic(val value: Int) {
    FOREIGN(0),
    DOMESTIC(1);

    companion object {
        fun from(value: Int): ProfileDomestic = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 내외국인 구분: $value")
    }
}