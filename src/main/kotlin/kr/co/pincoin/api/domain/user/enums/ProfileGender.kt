package kr.co.pincoin.api.domain.user.enums

enum class ProfileGender(val value: Int) {
    FEMALE(0),
    MALE(1);

    companion object {
        fun from(value: Int): ProfileGender = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 성별: $value")
    }
}