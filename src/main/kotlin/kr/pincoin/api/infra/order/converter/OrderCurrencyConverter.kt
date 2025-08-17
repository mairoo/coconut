package kr.pincoin.api.infra.order.converter

import jakarta.persistence.AttributeConverter
import jakarta.persistence.Converter
import kr.pincoin.api.domain.order.enums.OrderCurrency

@Converter(autoApply = true)
class OrderCurrencyConverter : AttributeConverter<OrderCurrency, Int> {
    override fun convertToDatabaseColumn(attribute: OrderCurrency?): Int? =
        attribute?.value

    override fun convertToEntityAttribute(dbData: Int?): OrderCurrency? =
        dbData?.let { OrderCurrency.fromValue(it) }
}