package kr.co.pincoin.api.domain.inquiry.enums

enum class CustomerQuestionCategory(val value: Int) {
    REGISTRATION(0),
    VERIFICATION(1),
    ORDER(2),
    PAYMENT(3),
    DELIVERY(4);

    companion object {
        fun from(value: Int): CustomerQuestionCategory = entries.find { it.value == value }
            ?: throw IllegalArgumentException("잘못된 자주묻는질문 답변 분류")
    }
}