package kr.co.pincoin.api.domain.message.enums

enum class NoticeMessageCategory(val value: Int) {
    COMMON(0),
    EVENT(1),
    PRICE(2);

    companion object {
        fun from(value: Int) = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 공지 분류")
    }
}