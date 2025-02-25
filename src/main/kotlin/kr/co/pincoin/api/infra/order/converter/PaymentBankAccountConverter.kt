package kr.co.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.co.pincoin.api.domain.order.enums.PaymentBankAccount

@Converter
class PaymentBankAccountConverter : AttributeConverter<PaymentBankAccount, Int> {
    override fun convertToDatabaseColumn(attribute: PaymentBankAccount): Int =
        attribute.value

    override fun convertToEntityAttribute(dbData: Int): PaymentBankAccount =
        PaymentBankAccount.from(dbData)
}